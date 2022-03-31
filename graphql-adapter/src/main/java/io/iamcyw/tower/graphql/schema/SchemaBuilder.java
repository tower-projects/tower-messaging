package io.iamcyw.tower.graphql.schema;

@Deprecated
public class SchemaBuilder {
    // private static final Logger LOG = Logger.getLogger(
    //         SchemaBuilder.class.getName());
    //
    // private final InputTypeCreator inputTypeCreator;
    // private final TypeCreator typeCreator;
    // private final FieldCreator fieldCreator;
    // private final InterfaceCreator interfaceCreator;
    // private final EnumCreator enumCreator;
    // private final ReferenceCreator referenceCreator;
    // private final OperationCreator operationCreator;
    // private final DirectiveTypeCreator directiveTypeCreator;
    //
    // /**
    //  * This builds the Schema from Jandex
    //  *
    //  * @param index the Jandex index
    //  * @return the Schema
    //  */
    // public static Schema build(IndexView index) {
    //     return build(index, TypeAutoNameStrategy.Default);
    // }
    //
    // /**
    //  * This builds the Schema from Jandex
    //  *
    //  * @param index the Jandex index
    //  * @param autoNameStrategy the naming strategy
    //  * @return the Schema
    //  */
    // public static Schema build(IndexView index, TypeAutoNameStrategy autoNameStrategy) {
    //     ScanningContext.register(index);
    //     return new SchemaBuilder(autoNameStrategy).generateSchema();
    // }
    //
    // private SchemaBuilder(TypeAutoNameStrategy autoNameStrategy) {
    //     enumCreator = new EnumCreator(autoNameStrategy);
    //     referenceCreator = new ReferenceCreator(autoNameStrategy);
    //     fieldCreator = new FieldCreator(referenceCreator);
    //     ArgumentCreator argumentCreator = new ArgumentCreator(referenceCreator);
    //
    //     inputTypeCreator = new InputTypeCreator(fieldCreator);
    //     operationCreator = new OperationCreator(referenceCreator, argumentCreator);
    //     typeCreator = new TypeCreator(referenceCreator, fieldCreator, operationCreator);
    //     interfaceCreator = new InterfaceCreator(referenceCreator, fieldCreator, operationCreator);
    //     directiveTypeCreator = new DirectiveTypeCreator(referenceCreator);
    // }
    //
    // private Schema generateSchema() {
    //
    //     // Get all the @GraphQLAPI annotations
    //     Collection<AnnotationInstance> graphQLApiAnnotations = ScanningContext.getIndex()
    //                                                                           .getAnnotations(
    //                                                                                   Annotations.GRAPHQL_API);
    //
    //     final Schema schema = new Schema();
    //
    //     addDirectiveTypes(schema);
    //     setupDirectives(new Directives(schema.getDirectiveTypes()));
    //
    //     for (AnnotationInstance graphQLApiAnnotation : graphQLApiAnnotations) {
    //         ClassInfo apiClass = graphQLApiAnnotation.target().asClass();
    //         List<MethodInfo> methods = apiClass.methods();
    //         Optional<Group> group = GroupHelper.getGroup(graphQLApiAnnotation);
    //         addOperations(group, schema, methods);
    //     }
    //
    //     // The above queries and mutations reference some models (input / type / interfaces / enum), let's create
    //     those
    //     addTypesToSchema(schema);
    //
    //     // We might have missed something
    //     addOutstandingTypesToSchema(schema);
    //
    //     // Add all annotated errors (Exceptions)
    //     addErrors(schema);
    //
    //     // Add all custom datafetchers
    //     addDataFetchers(schema);
    //
    //     // Reset the maps.
    //     referenceCreator.clear();
    //
    //     return schema;
    // }
    //
    // private void addDirectiveTypes(Schema schema) {
    //     for (AnnotationInstance annotationInstance : ScanningContext.getIndex().getAnnotations(DIRECTIVE)) {
    //         ClassInfo classInfo = annotationInstance.target().asClass();
    //         schema.addDirectiveType(directiveTypeCreator.create(classInfo));
    //     }
    // }
    //
    // private void setupDirectives(Directives directives) {
    //     typeCreator.setDirectives(directives);
    //     interfaceCreator.setDirectives(directives);
    //     fieldCreator.setDirectives(directives);
    // }
    //
    // private void addTypesToSchema(Schema schema) {
    //     // Add the input types
    //     createAndAddToSchema(ReferenceType.INPUT, inputTypeCreator, schema::addInput);
    //
    //     // Add the output types
    //     createAndAddToSchema(ReferenceType.TYPE, typeCreator, schema::addType);
    //
    //     // Add the interface types
    //     createAndAddToSchema(ReferenceType.INTERFACE, interfaceCreator, schema::addInterface);
    //
    //     // Add the enum types
    //     createAndAddToSchema(ReferenceType.ENUM, enumCreator, schema::addEnum);
    // }
    //
    // private void addOutstandingTypesToSchema(Schema schema) {
    //     boolean keepGoing = false;
    //
    //     // See if there is any inputs we missed
    //     if (findOutstandingAndAddToSchema(ReferenceType.INPUT, inputTypeCreator, schema::containsInput,
    //     schema::addInput)) {
    //         keepGoing = true;
    //     }
    //
    //     // See if there is any types we missed
    //     if (findOutstandingAndAddToSchema(ReferenceType.TYPE, typeCreator, schema::containsType, schema::addType)) {
    //         keepGoing = true;
    //     }
    //
    //     // See if there is any interfaces we missed
    //     if (findOutstandingAndAddToSchema(ReferenceType.INTERFACE, interfaceCreator, schema::containsInterface,
    //                                       schema::addInterface)) {
    //         keepGoing = true;
    //     }
    //
    //     // See if there is any enums we missed
    //     if (findOutstandingAndAddToSchema(ReferenceType.ENUM, enumCreator, schema::containsEnum,
    //                                       schema::addEnum)) {
    //         keepGoing = true;
    //     }
    //
    //     // If we missed something, that something might have created types we do not know about yet, so continue
    //     until we have everything
    //     if (keepGoing) {
    //         addOutstandingTypesToSchema(schema);
    //     }
    // }
    //
    // private void addErrors(Schema schema) {
    //     // Collection<AnnotationInstance> errorAnnotations = ScanningContext.getIndex().getAnnotations(
    //     //         Annotations.ERROR_CODE);
    //     // if (errorAnnotations != null && !errorAnnotations.isEmpty()) {
    //     //     for (AnnotationInstance errorAnnotation : errorAnnotations) {
    //     //         AnnotationTarget annotationTarget = errorAnnotation.target();
    //     //         if (annotationTarget.kind().equals(AnnotationTarget.Kind.CLASS)) {
    //     //             ClassInfo exceptionClass = annotationTarget.asClass();
    //     //             AnnotationValue value = errorAnnotation.value();
    //     //             if (value != null && value.asString() != null && !value.asString().isEmpty()) {
    //     //                 schema.addError(new ErrorInfo(exceptionClass.name().toString(), value.asString()));
    //     //             } else {
    //     //                 LOG.warn("Ignoring @ErrorCode on " + annotationTarget + " - Annotation value is not set");
    //     //             }
    //     //         } else {
    //     //             LOG.warn("Ignoring @ErrorCode on " + annotationTarget + " - Wrong target, only apply to
    //     CLASS ["
    //     //                              + annotationTarget.kind().toString() + "]");
    //     //         }
    //     //     }
    //     // }
    // }
    //
    // private void addDataFetchers(Schema schema) {
    //     // Collection<AnnotationInstance> datafetcherAnnotations = ScanningContext.getIndex()
    //     //                                                                        .getAnnotations(Annotations
    //     .DATAFETCHER);
    //     // if (datafetcherAnnotations != null && !datafetcherAnnotations.isEmpty()) {
    //     //     for (AnnotationInstance datafetcherAnnotation : datafetcherAnnotations) {
    //     //         AnnotationTarget annotationTarget = datafetcherAnnotation.target();
    //     //         if (annotationTarget.kind().equals(AnnotationTarget.Kind.CLASS)) {
    //     //             ClassInfo datafetcherClass = annotationTarget.asClass();
    //     //
    //     //             AnnotationValue forClass = datafetcherAnnotation.value("forClass");
    //     //
    //     //             AnnotationValue isWrapped = datafetcherAnnotation.value("isWrapped");
    //     //
    //     //             LOG.info("Adding custom datafetcher for " + forClass.asClass().name().toString() + " ["
    //     //                              + datafetcherClass.simpleName() + "]");
    //     //
    //     //             if (isWrapped != null && isWrapped.asBoolean()) {
    //     //                 // Wrapped
    //     //                 schema.addWrappedDataFetcher(forClass.asClass().name().toString(), datafetcherClass
    //     .simpleName());
    //     //             } else {
    //     //                 // Field
    //     //                 schema.addFieldDataFetcher(forClass.asClass().name().toString(), datafetcherClass
    //     .simpleName());
    //     //             }
    //     //
    //     //         }
    //     //     }
    //     // }
    // }
    //
    // private <T> void createAndAddToSchema(ReferenceType referenceType, Creator<T> creator, Consumer<T> consumer) {
    //     Queue<Reference> queue = referenceCreator.values(referenceType);
    //     while (!queue.isEmpty()) {
    //         Reference reference = queue.poll();
    //         ClassInfo classInfo = ScanningContext.getIndex().getClassByName(DotName.createSimple(reference
    //         .getClassName()));
    //         consumer.accept(creator.create(classInfo, reference));
    //     }
    // }
    //
    // private <T> boolean findOutstandingAndAddToSchema(ReferenceType referenceType, Creator<T> creator,
    //                                                   Predicate<String> contains, Consumer<T> consumer) {
    //
    //     boolean keepGoing = false;
    //     // Let's see what still needs to be done.
    //     Queue<Reference> values = referenceCreator.values(referenceType);
    //     while (!values.isEmpty()) {
    //         Reference reference = values.poll();
    //         ClassInfo classInfo = ScanningContext.getIndex().getClassByName(DotName.createSimple(reference
    //         .getClassName()));
    //         if (!contains.test(reference.getName())) {
    //             consumer.accept(creator.create(classInfo, reference));
    //             keepGoing = true;
    //         }
    //     }
    //
    //     return keepGoing;
    // }
    //
    // /**
    //  * This inspect all method, looking for Query and Mutation annotations,
    //  * to create those Operations.
    //  *
    //  * @param schema the schema to add the operation to.
    //  * @param methodInfoList the java methods.
    //  */
    // private void addOperations(Optional<Group> group, Schema schema, List<MethodInfo> methodInfoList) {
    //     for (MethodInfo methodInfo : methodInfoList) {
    //         Annotations annotationsForMethod = Annotations.getAnnotationsForMethod(methodInfo);
    //         if (annotationsForMethod.containsOneOfTheseAnnotations(Annotations.QUERY)) {
    //             Operation query = operationCreator.createOperation(methodInfo, OperationType.QUERY, null);
    //             if (group.isPresent()) {
    //                 schema.addGroupedQuery(group.get(), query);
    //             } else {
    //                 schema.addQuery(query);
    //             }
    //         }
    //         // else if (annotationsForMethod.containsOneOfTheseAnnotations(
    //         //         Annotations.MUTATION)) {
    //         //     Operation mutation = operationCreator.createOperation(methodInfo, OperationType.MUTATION, null);
    //         //     if (group.isPresent()) {
    //         //         schema.addGroupedMutation(group.get(), mutation);
    //         //     } else {
    //         //         schema.addMutation(mutation);
    //         //     }
    //         // }
    //         // else if (annotationsForMethod.containsOneOfTheseAnnotations(Annotations.SUBCRIPTION)) {
    //         //     Operation subscription = operationCreator.createOperation(methodInfo, OperationType
    //         .SUBSCRIPTION, null);
    //         //     if (group.isPresent()) {
    //         //         schema.addGroupedSubscription(group.get(), subscription);
    //         //     } else {
    //         //         schema.addSubscription(subscription);
    //         //     }
    //         // }
    //     }
    // }
}
