package io.iamcyw.tower.schema.creator;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.helper.Direction;
import io.iamcyw.tower.schema.helper.MethodHelper;
import io.iamcyw.tower.schema.model.*;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

/**
 * Creates a Operation object
 */
public class OperationCreator extends ModelCreator {

    private final ArgumentCreator argumentCreator;

    public OperationCreator(ReferenceCreator referenceCreator, ArgumentCreator argumentCreator) {
        super(referenceCreator);
        this.argumentCreator = argumentCreator;
    }

    /**
     * Get the name from annotation(s) or default.
     * This is for operations (query, mutation and source)
     *
     * @param methodInfo    the java method
     * @param operationType the type (query, mutation)
     * @param annotations   the annotations on this method
     * @return the operation name
     */
    private static String getOperationName(MethodInfo methodInfo, OperationType operationType,
                                           Annotations annotations) {
        DotName operationAnnotation = getOperationAnnotation(operationType);

        // If the @Query or @Command annotation has a value, use that, else use name or jsonb property
        return annotations.getOneOfTheseMethodAnnotationsValue(operationAnnotation, Annotations.JSONB_PROPERTY,
                                                               Annotations.JACKSON_PROPERTY)
                          .orElse(getDefaultExecutionTypeName(methodInfo, operationType));

    }

    private static DotName getOperationAnnotation(OperationType operationType) {
        switch (operationType) {
            case QUERY:
                return Annotations.QUERY;
            case COMMAND:
                return Annotations.COMMAND;
            case PREDICATE:
                return Annotations.PREDICATE;
            default:
                break;
        }
        return null;
    }

    private static String getDefaultExecutionTypeName(MethodInfo methodInfo, OperationType operationType) {
        String methodName = methodInfo.name();
        if (operationType.equals(OperationType.QUERY) || operationType.equals(OperationType.COMMAND)) {
            methodName = MethodHelper.getPropertyName(Direction.OUT, methodName);
        }
        return methodName;
    }

    /**
     * This creates a single operation.
     * It translate to one entry under a query / mutation in the schema or
     * one method in the Java class annotated with Query or Mutation
     *
     * @param methodInfo    the java method
     * @param operationType the type of operation (Query / Mutation)
     * @return a Operation that defines this GraphQL Operation
     */
    public Operation createOperation(MethodInfo methodInfo, OperationType operationType) {

        if (!Modifier.isPublic(methodInfo.flags())) {
            throw new IllegalArgumentException(
                    "Method " + methodInfo.declaringClass().name().toString() + "#" + methodInfo.name() +
                            " is used as an operation, but is not public");
        }

        Annotations annotationsForMethod = Annotations.getAnnotationsForMethod(methodInfo);
        Annotations annotationsForClass = Annotations.getAnnotationsForClass(methodInfo.declaringClass());

        Type fieldType = getReturnType(methodInfo);

        // Name
        String name = getOperationName(methodInfo, operationType, annotationsForMethod);

        // Field Type
        // validateFieldType(methodInfo, operationType);
        Reference reference = referenceCreator.createReferenceForOperationField(fieldType, annotationsForMethod);

        // Execution
        Execute execute = getExecution(annotationsForMethod, annotationsForClass, null);
        Operation operation = new Operation(methodInfo.declaringClass().name().toString(), methodInfo.name(),
                                            MethodHelper.getPropertyName(Direction.OUT, methodInfo.name()), name,
                                            reference, operationType, execute);

        // Arguments
        List<Type> parameters = methodInfo.parameters();
        for (short i = 0; i < parameters.size(); i++) {
            Optional<Argument> maybeArgument = argumentCreator.createArgument(operation, methodInfo, i);
            maybeArgument.ifPresent(operation::addArgument);
        }

        // Parameter
        annotationsForMethod.getOneOfTheseAnnotations(Annotations.PARAMETER, Annotations.PARAMETERS)
                            .ifPresent(annotationInstance -> {
                                if (annotationInstance.name().equals(Annotations.PARAMETERS)) {
                                    AnnotationInstance[] annotationValues = annotationInstance.value().asNestedArray();
                                    for (AnnotationInstance annotationValue : annotationValues) {
                                        parameter(annotationValue, operation);
                                    }
                                } else {
                                    parameter(annotationInstance, operation);
                                }
                            });
        // annotationsForMethod.getOneOfTheseAnnotations(Annotations.PREDICATE)
        //                     .ifPresent(annotationInstance -> {
        //                         if (annotationInstance.name().equals(Annotations.PREDICATES)) {
        //                             AnnotationInstance[] annotationValues = annotationInstance.value()
        //                             .asNestedArray();
        //                             for (AnnotationInstance annotationValue : annotationValues) {
        //                                 operation.addPredicate(predicate(annotationValue));
        //                             }
        //                         } else {
        //                             operation.addPredicate(predicate(annotationInstance));
        //                         }
        //                     });

        populateField(Direction.OUT, operation, fieldType, annotationsForMethod);

        return operation;
    }

    private void parameter(AnnotationInstance parameter, Operation operation) {
        operation.addParameter(parameter.value().asString(), parameter.value("parameter").asString());
    }

    private Execute getExecution(Annotations annotationsForMethod, Annotations annotationsForClass,
                                 Reference reference) {
        // first check annotation on method
        if (annotationsForMethod.containsOneOfTheseAnnotations(Annotations.BLOCKING)) {
            return Execute.BLOCKING;
        } else if (annotationsForMethod.containsOneOfTheseAnnotations(Annotations.NON_BLOCKING)) {
            return Execute.NON_BLOCKING;
        }

        // then check annotation on class
        if (annotationsForClass.containsOneOfTheseAnnotations(Annotations.BLOCKING)) {
            return Execute.BLOCKING;
        } else if (annotationsForClass.containsOneOfTheseAnnotations(Annotations.NON_BLOCKING)) {
            return Execute.NON_BLOCKING;
        }

        // lastly use default based on return type
        return Execute.DEFAULT;
    }

}
