package io.iamcyw.tower.schema;

import io.iamcyw.tower.schema.creator.ArgumentCreator;
import io.iamcyw.tower.schema.creator.FieldCreator;
import io.iamcyw.tower.schema.creator.OperationCreator;
import io.iamcyw.tower.schema.creator.ReferenceCreator;
import io.iamcyw.tower.schema.creator.type.Creator;
import io.iamcyw.tower.schema.creator.type.InputTypeCreator;
import io.iamcyw.tower.schema.model.*;
import org.jboss.jandex.*;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class SchemaBuilder {
    private final OperationCreator operationCreator;

    private final FieldCreator fieldCreator;

    private final InputTypeCreator inputTypeCreator;

    private final ReferenceCreator referenceCreator;

    public SchemaBuilder(ReferenceCreator referenceCreator, OperationCreator operationCreator) {
        this.referenceCreator = referenceCreator;

        this.operationCreator = operationCreator;
        fieldCreator = new FieldCreator(referenceCreator);
        this.inputTypeCreator = new InputTypeCreator();
    }

    public static Schema build(IndexView index) {
        ScanningContext.register(index);

        ReferenceCreator referenceCreator = new ReferenceCreator();
        OperationCreator opc = new OperationCreator(referenceCreator, new ArgumentCreator(referenceCreator));

        return new SchemaBuilder(referenceCreator, opc).generateSchema();
    }

    public Schema generateSchema() {
        Collection<AnnotationInstance> useCaseAnnotations = ScanningContext.getIndex()
                                                                           .getAnnotations(Annotations.USECASE);

        final Schema schema = new Schema();

        for (AnnotationInstance graphQLApiAnnotation : useCaseAnnotations) {
            ClassInfo apiClass = graphQLApiAnnotation.target().asClass();
            List<MethodInfo> methods = apiClass.methods();
            addOperations(schema, methods);
        }

        // The above queries and mutations reference some models (input / type / interfaces / enum), let's create those
        addTypesToSchema(schema);

        return schema;
    }

    private void addTypesToSchema(Schema schema) {
        // Add the input types
        createAndAddToSchema(ReferenceType.INPUT, inputTypeCreator, schema::addInput);
    }

    private <T> void createAndAddToSchema(ReferenceType referenceType, Creator<T> creator, Consumer<T> consumer) {
        Queue<Reference> queue = referenceCreator.values(referenceType);
        while (!queue.isEmpty()) {
            Reference reference = queue.poll();
            ClassInfo classInfo = ScanningContext.getIndex()
                                                 .getClassByName(DotName.createSimple(reference.getClassName()));
            consumer.accept(creator.create(classInfo, reference));
        }
    }


    /**
     * This inspect all method, looking for Query and Mutation annotations,
     * to create those Operations.
     *
     * @param schema         the schema to add the operation to.
     * @param methodInfoList the java methods.
     */
    private void addOperations(Schema schema, List<MethodInfo> methodInfoList) {
        for (MethodInfo methodInfo : methodInfoList) {
            Annotations annotationsForMethod = Annotations.getAnnotationsForMethod(methodInfo);
            if (annotationsForMethod.containsOneOfTheseAnnotations(Annotations.QUERY)) {
                Operation query = operationCreator.createOperation(methodInfo, OperationType.QUERY);
                schema.addQuery(query);
            } else if (annotationsForMethod.containsOneOfTheseAnnotations(Annotations.COMMAND)) {
                Operation command = operationCreator.createOperation(methodInfo, OperationType.COMMAND);
                schema.addCommand(command);
            } else if (annotationsForMethod.containsOneOfTheseAnnotations(Annotations.PREDICATE)) {
                Operation predicate = operationCreator.createOperation(methodInfo, OperationType.PREDICATE);
                schema.addPredicate(predicate);
            }
        }
    }

}
