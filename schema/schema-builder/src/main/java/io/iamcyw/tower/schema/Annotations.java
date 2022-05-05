package io.iamcyw.tower.schema;

import org.jboss.jandex.*;
import org.jboss.jandex.AnnotationTarget.Kind;

import java.util.*;
import java.util.stream.Collectors;

/**
 * All the annotations we care about for a certain context
 * <p>
 * There are multiple static methods to create the annotations for the correct context
 */
public class Annotations {

    private static final short ZERO = 0;

    // SmallRye Common Annotations
    public static final DotName BLOCKING = DotName.createSimple("io.smallrye.common.annotation.Blocking");

    public static final DotName NON_BLOCKING = DotName.createSimple("io.smallrye.common.annotation.NonBlocking");


    // ------- All static creators done, now the actual class --------

    public static final DotName USECASE = DotName.createSimple("io.iamcyw.tower.messaging.UseCase");

    public static final DotName QUERY = DotName.createSimple("io.iamcyw.tower.messaging.QueryHandle");

    public static final DotName COMMAND = DotName.createSimple("io.iamcyw.tower.messaging.CommandHandle");

    public static final DotName PREDICATE = DotName.createSimple("io.iamcyw.tower.messaging.Predicate");

    public static final DotName PARAMETER = DotName.createSimple("io.iamcyw.tower.messaging.Parameter");

    public static final DotName PARAMETERS = DotName.createSimple("io.iamcyw.tower.messaging.Parameters");

    public static final DotName DESCRIPTION = DotName.createSimple("io.iamcyw.tower.messaging.Description");

    // Json-B Annotations
    public static final DotName JSONB_DATE_FORMAT = DotName.createSimple("javax.json.bind.annotation.JsonbDateFormat");

    public static final DotName JSONB_NUMBER_FORMAT = DotName.createSimple(
            "javax.json.bind.annotation.JsonbNumberFormat");

    public static final DotName JSONB_PROPERTY = DotName.createSimple("javax.json.bind.annotation.JsonbProperty");

    public static final DotName JSONB_TRANSIENT = DotName.createSimple("javax.json.bind.annotation.JsonbTransient");

    public static final DotName JSONB_CREATOR = DotName.createSimple("javax.json.bind.annotation.JsonbCreator");

    public static final DotName JSONB_TYPE_ADAPTER = DotName.createSimple(
            "javax.json.bind.annotation.JsonbTypeAdapter");

    // Jackson Annotations
    public static final DotName JACKSON_IGNORE = DotName.createSimple("com.fasterxml.jackson.annotation.JsonIgnore");

    public static final DotName JACKSON_PROPERTY = DotName.createSimple(
            "com.fasterxml.jackson.annotation.JsonProperty");

    public static final DotName JACKSON_CREATOR = DotName.createSimple("com.fasterxml.jackson.annotation.JsonCreator");

    // Private static methods use by the static initializers

    public static final DotName JACKSON_FORMAT = DotName.createSimple("com.fasterxml.jackson.annotation.JsonFormat");

    // Bean Validation Annotations (SmallRye extra, not part of the spec)
    public static final DotName BEAN_VALIDATION_NOT_NULL = DotName.createSimple("javax.validation.constraints.NotNull");

    public static final DotName BEAN_VALIDATION_NOT_EMPTY = DotName.createSimple(
            "javax.validation.constraints.NotEmpty");

    public static final DotName BEAN_VALIDATION_NOT_BLANK = DotName.createSimple(
            "javax.validation.constraints.NotBlank");

    //Kotlin NotNull
    public static final DotName KOTLIN_NOT_NULL = DotName.createSimple("org.jetbrains.annotations.NotNull");

    public final Map<DotName, AnnotationInstance> parentAnnotations;

    private final Map<DotName, AnnotationInstance> annotationsMap;

    private Annotations(Map<DotName, AnnotationInstance> annotations) {
        this(annotations, new HashMap<>());
    }

    /**
     * Create the annotations, mapped by name
     *
     * @param annotations the annotation
     */
    private Annotations(Map<DotName, AnnotationInstance> annotations,
                        Map<DotName, AnnotationInstance> parentAnnotations) {
        this.annotationsMap = annotations;
        this.parentAnnotations = parentAnnotations;
    }

    /**
     * Get used when creating operations.
     * Operation only have methods (no properties)
     *
     * @param methodInfo the java method
     * @return Annotations for this method and its return-type
     */
    public static Annotations getAnnotationsForMethod(MethodInfo methodInfo) {
        Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();

        for (AnnotationInstance annotationInstance : methodInfo.annotations()) {
            DotName name = annotationInstance.name();
            Kind kind = annotationInstance.target().kind();
            if (kind.equals(Kind.METHOD)) {
                annotationMap.put(name, annotationInstance);
            }
        }

        final Type type = methodInfo.returnType();
        if (Classes.isParameterized(type)) {
            Type wrappedType = type.asParameterizedType().arguments().get(0);
            for (final AnnotationInstance annotationInstance : wrappedType.annotations()) {
                DotName name = annotationInstance.name();
                annotationMap.put(name, annotationInstance);
            }
        }

        Map<DotName, AnnotationInstance> parentAnnotations = getParentAnnotations(methodInfo.declaringClass());

        return new Annotations(annotationMap, parentAnnotations);
    }

    private static Map<DotName, AnnotationInstance> getParentAnnotations(FieldInfo fieldInfo, MethodInfo methodInfo) {
        ClassInfo declaringClass = fieldInfo != null ? fieldInfo.declaringClass() : methodInfo.declaringClass();
        return getParentAnnotations(declaringClass);
    }

    private static Map<DotName, AnnotationInstance> getParentAnnotations(ClassInfo classInfo) {
        Map<DotName, AnnotationInstance> parentAnnotations = new HashMap<>();

        for (AnnotationInstance classAnnotation : classInfo.classAnnotations()) {
            parentAnnotations.putIfAbsent(classAnnotation.name(), classAnnotation);
        }

        Map<DotName, AnnotationInstance> packageAnnotations = getPackageAnnotations(classInfo);
        for (DotName dotName : packageAnnotations.keySet()) {
            parentAnnotations.putIfAbsent(dotName, packageAnnotations.get(dotName));
        }

        return parentAnnotations;
    }

    private static Map<DotName, AnnotationInstance> getPackageAnnotations(ClassInfo classInfo) {
        Map<DotName, AnnotationInstance> packageAnnotations = new HashMap<>();

        DotName packageName = packageInfo(classInfo);
        if (packageName != null) {
            ClassInfo packageInfo = ScanningContext.getIndex().getClassByName(packageName);
            if (packageInfo != null) {
                for (AnnotationInstance packageAnnotation : packageInfo.classAnnotations()) {
                    packageAnnotations.putIfAbsent(packageAnnotation.name(), packageAnnotation);
                }
            }
        }

        return packageAnnotations;
    }

    private static DotName packageInfo(ClassInfo classInfo) {
        String className = classInfo.name().toString();
        int index = className.lastIndexOf('.');
        if (index == -1) {
            return null;
        }
        return DotName.createSimple(className.substring(0, index) + ".package-info");
    }

    /**
     * Get used when we create types and references to them
     * <p>
     * Class level annotation for type creation.
     *
     * @param classInfo the java class
     * @return annotation for this class
     */
    public static Annotations getAnnotationsForClass(ClassInfo classInfo) {

        Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();

        for (AnnotationInstance annotationInstance : classInfo.classAnnotations()) {
            DotName name = annotationInstance.name();
            annotationMap.put(name, annotationInstance);
        }

        Map<DotName, AnnotationInstance> packageAnnotations = getPackageAnnotations(classInfo);
        for (DotName dotName : packageAnnotations.keySet()) {
            annotationMap.putIfAbsent(dotName, packageAnnotations.get(dotName));
        }

        return new Annotations(annotationMap, packageAnnotations);
    }

    /**
     * Get used when creating arrays.
     * <p>
     * This will contains the annotation on the collection field and method
     *
     * @param typeInCollection       the field java type
     * @param methodTypeInCollection the method java type
     * @return the annotation for this array
     */
    public static Annotations getAnnotationsForArray(Type typeInCollection, Type methodTypeInCollection) {
        Map<DotName, AnnotationInstance> annotationMap = getAnnotationsForType(typeInCollection);
        annotationMap.putAll(getAnnotationsForType(methodTypeInCollection));
        return new Annotations(annotationMap);
    }

    /**
     * Used when we are creating operation and arguments for these operations
     *
     * @param methodInfo the java method
     * @param pos        the argument position
     * @return annotation for this argument
     */
    public static Annotations getAnnotationsForArgument(MethodInfo methodInfo, short pos) {
        if (pos >= methodInfo.parameters().size()) {
            throw new IndexOutOfBoundsException(
                    "Parameter at position " + pos + " not found on method " + methodInfo.name());
        }

        final Type parameterType = methodInfo.parameters().get(pos);

        Map<DotName, AnnotationInstance> annotationMap = getAnnotations(parameterType);

        for (AnnotationInstance anno : methodInfo.annotations()) {
            if (anno.target().kind().equals(Kind.METHOD_PARAMETER)) {
                MethodParameterInfo methodParameter = anno.target().asMethodParameter();
                short position = methodParameter.position();
                if (position == pos) {
                    annotationMap.put(anno.name(), anno);
                }
            }
        }

        final Map<DotName, AnnotationInstance> parentAnnotations = getParentAnnotations(methodInfo.declaringClass());

        return new Annotations(annotationMap, parentAnnotations);
    }

    private static boolean isMethodAnnotation(AnnotationInstance instance) {
        return instance.target().kind().equals(Kind.METHOD);
    }

    private static boolean isMethodParameterAnnotation(AnnotationInstance instance) {
        return instance.target().kind().equals(Kind.METHOD_PARAMETER);
    }

    private static Map<DotName, AnnotationInstance> getAnnotations(Type type) {
        Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();

        if (type.kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
            Type typeInCollection = type.asParameterizedType().arguments().get(0);
            annotationMap.putAll(getAnnotations(typeInCollection));
        } else {
            List<AnnotationInstance> annotations = type.annotations();
            for (AnnotationInstance annotationInstance : annotations) {
                annotationMap.put(annotationInstance.name(), annotationInstance);
            }
        }

        return annotationMap;
    }

    private static Map<DotName, AnnotationInstance> getAnnotationsForType(Type type) {
        Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();
        for (AnnotationInstance annotationInstance : type.annotations()) {
            DotName name = annotationInstance.name();
            annotationMap.put(name, annotationInstance);
        }
        return annotationMap;
    }

    private static Map<DotName, AnnotationInstance> getAnnotationsForField(FieldInfo fieldInfo, MethodInfo methodInfo) {
        Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();
        if (fieldInfo != null)
            annotationMap.putAll(listToMap(
                    fieldInfo.annotations().stream().filter(ai -> ai.target().kind() == Kind.FIELD)
                             .collect(Collectors.toList())));
        if (methodInfo != null)
            annotationMap.putAll(listToMap(methodInfo.annotations().stream()
                                                     .filter(ai -> ai.target().kind() == Kind.METHOD ||
                                                             ai.target().kind() == Kind.METHOD_PARAMETER)
                                                     .collect(Collectors.toList())));
        return annotationMap;
    }

    private static Map<DotName, AnnotationInstance> listToMap(List<AnnotationInstance> annotationInstances) {
        Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();

        for (AnnotationInstance annotationInstance : annotationInstances) {
            DotName name = annotationInstance.name();
            annotationMap.put(name, annotationInstance);
        }
        return annotationMap;
    }

    private static Map<DotName, AnnotationInstance> getAnnotationsWithFilter(Type type, DotName... filter) {
        Map<DotName, AnnotationInstance> annotationMap = new HashMap<>();

        if (type.kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
            Type typeInCollection = type.asParameterizedType().arguments().get(0);
            annotationMap.putAll(getAnnotationsWithFilter(typeInCollection, filter));
        } else {
            List<AnnotationInstance> annotations = type.annotations();
            for (AnnotationInstance annotationInstance : annotations) {
                if (Arrays.asList(filter).contains(annotationInstance.name())) {
                    annotationMap.put(annotationInstance.name(), annotationInstance);
                }
            }
        }

        return annotationMap;
    }

    public Set<DotName> getAnnotationNames() {
        return annotationsMap.keySet();
    }

    public Annotations removeAnnotations(DotName... annotations) {
        Map<DotName, AnnotationInstance> newAnnotationsMap = new HashMap<>(annotationsMap);
        for (DotName annotation : annotations) {
            newAnnotationsMap.remove(annotation);
        }
        return new Annotations(newAnnotationsMap, this.parentAnnotations);
    }

    /**
     * Get a specific annotation
     *
     * @param annotation the annotation you want
     * @return the annotation value or null
     */
    public AnnotationValue getAnnotationValue(DotName annotation) {
        return this.annotationsMap.get(annotation).value();
    }

    /**
     * Get a specific annotation
     *
     * @param annotation the annotation you want
     * @return the annotation value or null
     */
    public AnnotationValue getAnnotationValue(DotName annotation, String name) {
        return this.annotationsMap.get(annotation).value(name);
    }

    /**
     * Check if there is an annotation and it has a valid value
     *
     * @param annotation the annotation we are checking
     * @return true if valid value
     */
    public boolean containsKeyAndValidValue(DotName annotation) {
        return this.annotationsMap.containsKey(annotation) && this.annotationsMap.get(annotation).value() != null;
    }

    /**
     * Check if one of these annotations is present
     *
     * @param annotations the annotations to check
     * @return true if it does
     */
    public boolean containsOneOfTheseAnnotations(DotName... annotations) {
        for (DotName name : annotations) {
            if (this.annotationsMap.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsOneOfTheseInheritableAnnotations(DotName... annotations) {
        for (DotName name : annotations) {
            if (this.parentAnnotations.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get on of these annotations
     *
     * @param annotations the annotations to check (in order)
     * @return the annotation potentially or empty if not found
     */
    public Optional<AnnotationInstance> getOneOfTheseAnnotations(DotName... annotations) {
        for (DotName name : annotations) {
            if (this.annotationsMap.containsKey(name)) {
                return Optional.of(this.annotationsMap.get(name));
            }
        }
        return Optional.empty();
    }

    /**
     * This go through a list of annotations and find the first one that has a valid value.
     * If it could not find one, it return empty
     *
     * @param annotations the annotations in order
     * @return the valid annotation value or default value
     */
    public Optional<String> getOneOfTheseAnnotationsValue(DotName... annotations) {
        for (DotName dotName : annotations) {
            if (dotName != null && containsKeyAndValidValue(dotName)) {
                return getStringValue(dotName);
            }
        }
        return Optional.empty();
    }

    /**
     * This go through a list of method annotations and find the first one that has a valid value.
     * If it could not find one, it return the default value.
     *
     * @param annotations the annotations in order
     * @return the valid annotation value or empty
     */
    public Optional<String> getOneOfTheseMethodAnnotationsValue(DotName... annotations) {
        for (DotName dotName : annotations) {
            if (dotName != null && hasValidMethodAnnotation(dotName)) {
                return getStringValue(dotName);
            }
        }
        return Optional.empty();
    }

    /**
     * This go through a list of method parameter annotations and find the first one that has a valid value.
     * If it could not find one, it return the default value.
     *
     * @param annotations the annotations in order
     * @return the valid annotation value or empty
     */
    public Optional<String> getOneOfTheseMethodParameterAnnotationsValue(DotName... annotations) {
        for (DotName dotName : annotations) {
            if (dotName != null && hasValidMethodParameterAnnotation(dotName)) {
                return getStringValue(dotName);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return annotationsMap.toString();
    }

    private boolean hasValidMethodAnnotation(DotName annotation) {
        return containsKeyAndValidValue(annotation) && isMethodAnnotation(getAnnotation(annotation));
    }

    private boolean hasValidMethodParameterAnnotation(DotName annotation) {
        return containsKeyAndValidValue(annotation) && isMethodParameterAnnotation(getAnnotation(annotation));
    }

    private AnnotationInstance getAnnotation(DotName key) {
        return this.annotationsMap.get(key);
    }

    private Optional<String> getStringValue(DotName annotation) {
        AnnotationInstance annotationInstance = getAnnotation(annotation);
        if (annotationInstance != null) {
            return getStringValue(annotationInstance);
        }
        return Optional.empty();
    }

    private Optional<String> getStringValue(AnnotationInstance annotationInstance) {
        AnnotationValue value = annotationInstance.value();
        if (value != null) {
            return getStringValue(value);
        }
        return Optional.empty();
    }

    private Optional<String> getStringValue(AnnotationValue annotationValue) {
        String value;
        if (annotationValue != null) {
            value = annotationValue.asString();
            if (value != null && !value.isEmpty()) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

}
