package io.iamcyw.tower.graphql.schema;

@Deprecated
public class Annotations {

    // //@formatter:off
    // public static final DotName GRAPHQL_API = DotName.createSimple("io.iamcyw.tower.messaging.MessageApi");
    // public static final DotName QUERY = DotName.createSimple("io.iamcyw.tower.queryhandling.QueryHandle");
    // //@formatter:on
    //
    // public final Map<DotName, AnnotationInstance> parentAnnotations;
    //
    // private final Map<DotName, AnnotationInstance> annotationsMap;
    //
    // private Annotations(Map<DotName, AnnotationInstance> annotations,
    //                     Map<DotName, AnnotationInstance> parentAnnotations) {
    //     this.annotationsMap = annotations;
    //     this.parentAnnotations = parentAnnotations;
    // }
    //
    // public static Annotations getAnnotationsForMethod(MethodInfo methodInfo) {
    //     Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();
    //
    //     for (AnnotationInstance annotationInstance : methodInfo.annotations()) {
    //         DotName name = annotationInstance.name();
    //         AnnotationTarget.Kind kind = annotationInstance.target().kind();
    //         if (kind.equals(AnnotationTarget.Kind.METHOD)) {
    //             annotationMap.put(name, annotationInstance);
    //         }
    //     }
    //
    //     final Type type = methodInfo.returnType();
    //     if (Classes.isParameterized(type)) {
    //         Type wrappedType = type.asParameterizedType().arguments().get(0);
    //         for (final AnnotationInstance annotationInstance : wrappedType.annotations()) {
    //             DotName name = annotationInstance.name();
    //             annotationMap.put(name, annotationInstance);
    //         }
    //     }
    //
    //     Map<DotName, AnnotationInstance> parentAnnotations = getParentAnnotations(methodInfo.declaringClass());
    //
    //     return new Annotations(annotationMap, parentAnnotations);
    // }
    //
    // private static Map<DotName, AnnotationInstance> getParentAnnotations(FieldInfo fieldInfo, MethodInfo
    // methodInfo) {
    //     ClassInfo declaringClass = fieldInfo != null ? fieldInfo.declaringClass() : methodInfo.declaringClass();
    //     return getParentAnnotations(declaringClass);
    // }
    //
    // private static Map<DotName, AnnotationInstance> getParentAnnotations(ClassInfo classInfo) {
    //     Map<DotName, AnnotationInstance> parentAnnotations = new HashMap<>();
    //
    //     for (AnnotationInstance classAnnotation : classInfo.classAnnotations()) {
    //         parentAnnotations.putIfAbsent(classAnnotation.name(), classAnnotation);
    //     }
    //
    //     Map<DotName, AnnotationInstance> packageAnnotations = getPackageAnnotations(classInfo);
    //     for (DotName dotName : packageAnnotations.keySet()) {
    //         parentAnnotations.putIfAbsent(dotName, packageAnnotations.get(dotName));
    //     }
    //
    //     return parentAnnotations;
    // }
    //
    // private static Map<DotName, AnnotationInstance> getPackageAnnotations(ClassInfo classInfo) {
    //     Map<DotName, AnnotationInstance> packageAnnotations = new HashMap<>();
    //
    //     DotName packageName = packageInfo(classInfo);
    //     if (packageName != null) {
    //         ClassInfo packageInfo = ScanningContext.getIndex().getClassByName(packageName);
    //         if (packageInfo != null) {
    //             for (AnnotationInstance packageAnnotation : packageInfo.classAnnotations()) {
    //                 packageAnnotations.putIfAbsent(packageAnnotation.name(), packageAnnotation);
    //             }
    //         }
    //     }
    //
    //     return packageAnnotations;
    // }
    //
    // private static DotName packageInfo(ClassInfo classInfo) {
    //     String className = classInfo.name().toString();
    //     int index = className.lastIndexOf('.');
    //     if (index == -1) {
    //         return null;
    //     }
    //     return DotName.createSimple(className.substring(0, index) + ".package-info");
    // }
    //
    // public boolean containsOneOfTheseAnnotations(DotName... annotations) {
    //     for (DotName name : annotations) {
    //         if (this.annotationsMap.containsKey(name)) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }
    // public static final DotName MUTATION = DotName.createSimple("org.eclipse.microprofile.graphql.Mutation");
    // public static final DotName INPUT = DotName.createSimple("org.eclipse.microprofile.graphql.Input");
    // public static final DotName TYPE = DotName.createSimple("org.eclipse.microprofile.graphql.Type");
    // public static final DotName INTERFACE = DotName.createSimple("org.eclipse.microprofile.graphql.Interface");
    // public static final DotName ENUM = DotName.createSimple("org.eclipse.microprofile.graphql.Enum");
    // public static final DotName ID = DotName.createSimple("org.eclipse.microprofile.graphql.Id");
    // public static final DotName DESCRIPTION = DotName.createSimple("org.eclipse.microprofile.graphql.Description");
    // public static final DotName DATE_FORMAT = DotName.createSimple("org.eclipse.microprofile.graphql.DateFormat");
    // public static final DotName NUMBER_FORMAT = DotName.createSimple("org.eclipse.microprofile.graphql
    // .NumberFormat");
    // public static final DotName DEFAULT_VALUE = DotName.createSimple("org.eclipse.microprofile.graphql
    // .DefaultValue");
    // public static final DotName IGNORE = DotName.createSimple("org.eclipse.microprofile.graphql.Ignore");
    // public static final DotName NON_NULL = DotName.createSimple("org.eclipse.microprofile.graphql.NonNull");
    // public static final DotName NAME = DotName.createSimple("org.eclipse.microprofile.graphql.Name");
    // public static final DotName SOURCE = DotName.createSimple("org.eclipse.microprofile.graphql.Source");
}
