package io.iamcyw.tower.schema;

import io.iamcyw.tower.schema.creator.ArgumentCreator;
import io.iamcyw.tower.schema.creator.OperationCreator;
import io.iamcyw.tower.schema.creator.ReferenceCreator;
import io.iamcyw.tower.schema.helper.TypeAutoNameStrategy;
import io.iamcyw.tower.schema.model.Operation;
import io.iamcyw.tower.schema.model.OperationType;
import io.iamcyw.tower.schema.model.Schema;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;

import java.util.Collection;
import java.util.List;

public class SchemaBuilder {
    private final OperationCreator operationCreator;

    private final ReferenceCreator referenceCreator;


    private SchemaBuilder(TypeAutoNameStrategy autoNameStrategy) {
        referenceCreator = new ReferenceCreator();

        ArgumentCreator argumentCreator = new ArgumentCreator(referenceCreator);

        operationCreator = new OperationCreator(referenceCreator, argumentCreator);
    }

    public static Schema build(IndexView index) {
        return build(index, TypeAutoNameStrategy.Default);
    }

    public static Schema build(IndexView index, TypeAutoNameStrategy autoNameStrategy) {
        ScanningContext.register(index);
        return new SchemaBuilder(autoNameStrategy).generateSchema();
    }

    private Schema generateSchema() {

        Collection<AnnotationInstance> useCaseAnnotations = ScanningContext.getIndex()
                                                                           .getAnnotations(Annotations.USECASE);

        final Schema schema = new Schema();

        for (AnnotationInstance graphQLApiAnnotation : useCaseAnnotations) {
            ClassInfo apiClass = graphQLApiAnnotation.target().asClass();
            List<MethodInfo> methods = apiClass.methods();
            addOperations(schema, methods);
        }

        return schema;
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
