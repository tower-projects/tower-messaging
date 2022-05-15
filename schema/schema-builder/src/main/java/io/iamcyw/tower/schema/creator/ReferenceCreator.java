package io.iamcyw.tower.schema.creator;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.Classes;
import io.iamcyw.tower.schema.ScanningContext;
import io.iamcyw.tower.schema.SchemaBuilderException;
import io.iamcyw.tower.schema.helper.Direction;
import io.iamcyw.tower.schema.helper.TypeNameHelper;
import io.iamcyw.tower.schema.model.Reference;
import io.iamcyw.tower.schema.model.ReferenceType;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.logging.Logger;

import java.util.*;

public class ReferenceCreator {

    private static final Logger LOG = Logger.getLogger(ReferenceCreator.class.getName());

    private final Queue<Reference> inputReferenceQueue = new ArrayDeque<>();

    private final Queue<Reference> typeReferenceQueue = new ArrayDeque<>();

    // Some maps we populate during scanning
    private final Map<String, Reference> inputReferenceMap = new HashMap<>();

    private final Map<String, Reference> typeReferenceMap = new HashMap<>();

    private static ReferenceType getCorrectReferenceType(Direction direction) {
        if (direction.equals(Direction.IN)) {
            return ReferenceType.INPUT;
        } else {
            return ReferenceType.TYPE;
        }
    }

    /**
     * Get the values for a certain type
     *
     * @param referenceType the type
     * @return the references
     */
    public Queue<Reference> values(ReferenceType referenceType) {
        return getReferenceQueue(referenceType);
    }

    private Queue<Reference> getReferenceQueue(ReferenceType referenceType) {
        switch (referenceType) {
            case INPUT:
                return inputReferenceQueue;
            case TYPE:
                return typeReferenceQueue;
            default:
                return null;
        }
    }

    private void putIfAbsent(String key, Reference reference, ReferenceType referenceType) {
        Map<String, Reference> map = getReferenceMap(referenceType);
        Queue<Reference> queue = getReferenceQueue(referenceType);
        if (map != null && queue != null) {
            if (!map.containsKey(key)) {
                map.put(key, reference);
                queue.add(reference);
            } else {
                String existingClass = map.get(key).getClassName();
                String newClass = reference.getClassName();
                if (!existingClass.equals(newClass)) {
                    throw new SchemaBuilderException(
                            "Classes " + existingClass + " and " + newClass + " map to the same GraphQL type '" + key +
                                    "', " + "consider using the @Name annotation or a different naming strategy to " +
                                    "distinguish between them");
                }
            }
        }
    }

    private Map<String, Reference> getReferenceMap(ReferenceType referenceType) {
        switch (referenceType) {
            // case ENUM:
            //     return enumReferenceMap;
            case INPUT:
                return inputReferenceMap;
            // case INTERFACE:
            //     return interfaceReferenceMap;
            case TYPE:
                return typeReferenceMap;
            default:
                return null;
        }
    }

    /**
     * Clear the scanned references. This is done when we created all references and do not need to remember what to
     * scan.
     */
    public void clear() {
        inputReferenceMap.clear();

        inputReferenceQueue.clear();
    }

    public Reference createReferenceForOperationField(Type fieldType, Annotations annotationsForMethod) {
        return getReference(Direction.OUT, null, fieldType, annotationsForMethod);
    }

    public Reference createReferenceForOperationArgument(Type argumentType, Annotations annotationsForThisArgument) {
        return getReference(Direction.IN, null, argumentType, annotationsForThisArgument);
    }

    private Reference getReference(Direction direction, Type fieldType, Type methodType, Annotations annotations) {
        return getReference(direction, fieldType, methodType, annotations, null);
    }

    /**
     * Get a reference to a field (method response) on an interface
     * <p>
     * Interfaces is only usable on Type, so the direction in OUT.
     *
     * @param methodType               the method response type
     * @param annotationsForThisMethod annotations on this method
     * @return a reference to the type
     */
    public Reference createReferenceForInterfaceField(Type methodType, Annotations annotationsForThisMethod,
                                                      Reference parentObjectReference) {
        return getReference(Direction.OUT, null, methodType, annotationsForThisMethod, parentObjectReference);
    }

    public Reference createReferenceForPojoField(Direction direction, Type fieldType, Type methodType,
                                                 Annotations annotations, Reference parentObjectReference) {
        return getReference(direction, fieldType, methodType, annotations, parentObjectReference);
    }


    private Reference getReference(Direction direction, Type fieldType, Type methodType, Annotations annotations,
                                   Reference parentObjectReference) {
        // In some case, like operations and interfaces, there is no fieldType
        if (fieldType == null) {
            fieldType = methodType;
        }

        String fieldTypeName = fieldType.name().toString();

        if (fieldType.kind().equals(Type.Kind.VOID)) {
            return new Reference(void.class.getName(), "void", getCorrectReferenceType(direction));
        } else if (fieldType.kind().equals(Type.Kind.PRIMITIVE)) {
            return getNonIndexedReference(direction, fieldType);
        } else if (fieldType.kind().equals(Type.Kind.ARRAY)) {
            // Java Array
            Type typeInArray = fieldType.asArrayType().component();
            Type typeInMethodArray = methodType.asArrayType().component();
            return getReference(direction, typeInArray, typeInMethodArray, annotations, parentObjectReference);
        } else if (Classes.isCollection(fieldType) || Classes.isUnwrappedType(fieldType)) {
            // Collections and unwrapped types
            Type typeInCollection = fieldType.asParameterizedType().arguments().get(0);
            Type typeInMethodCollection = methodType.asParameterizedType().arguments().get(0);
            return getReference(direction, typeInCollection, typeInMethodCollection, annotations,
                                parentObjectReference);
        } else if (Classes.isMap(fieldType)) {
            ParameterizedType parameterizedFieldType = fieldType.asParameterizedType();
            List<Type> fieldArguments = parameterizedFieldType.arguments();

            Map<String, Reference> parametrizedTypeArgumentsReferences = new HashMap<>();
            parametrizedTypeArgumentsReferences.put("key", getReference(direction, fieldArguments.get(0), methodType,
                                                                        annotations));
            parametrizedTypeArgumentsReferences.put("value", getReference(direction, fieldArguments.get(1), methodType,
                                                                          annotations));

            Reference reference = new Reference(Map.class.getName(), "Map", getCorrectReferenceType(direction),
                                                parametrizedTypeArgumentsReferences, true);

            return reference;
        } else if (fieldType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
            // Type.Kind.PARAMETERIZED_TYPE handles generics PoJos here, collections and unwrapped types are catched
            // before.
            // We have to add parametrized types into returned reference object name, and also store parameter types
            // into reference so they can be used for later processing of types
            ClassInfo classInfo = ScanningContext.getIndex().getClassByName(fieldType.name());
            if (classInfo != null) {

                List<Type> parametrizedTypeArguments = fieldType.asParameterizedType().arguments();
                Map<String, Reference> parametrizedTypeArgumentsReferences = collectParametrizedTypes(classInfo,
                                                                                                      parametrizedTypeArguments,
                                                                                                      direction,
                                                                                                      parentObjectReference);

                return createReference(direction, classInfo, true, true, parentObjectReference,
                                       parametrizedTypeArgumentsReferences, true);
            } else {
                return getNonIndexedReference(direction, fieldType);
            }
        } else if (fieldType.kind().equals(Type.Kind.CLASS)) {
            ClassInfo classInfo = ScanningContext.getIndex().getClassByName(fieldType.name());
            if (classInfo != null) {

                Map<String, Reference> parametrizedTypeArgumentsReferences = null;

                ParameterizedType parametrizedParentType = findParametrizedParentType(classInfo);
                if (parametrizedParentType != null) {
                    ClassInfo ci = ScanningContext.getIndex().getClassByName(parametrizedParentType.name());
                    if (ci == null) {
                        throw new SchemaBuilderException("No class info found for parametrizedParentType name [" +
                                                                 parametrizedParentType.name() + "]");
                    }

                    parametrizedTypeArgumentsReferences = collectParametrizedTypes(ci,
                                                                                   parametrizedParentType.arguments(),
                                                                                   direction, parentObjectReference);
                }

                return createReference(direction, classInfo, true, true, parentObjectReference,
                                       parametrizedTypeArgumentsReferences, false);
            } else {
                return getNonIndexedReference(direction, fieldType);
            }
        } else if (fieldType.kind().equals(Type.Kind.TYPE_VARIABLE)) {
            if (parentObjectReference == null || parentObjectReference.getParametrizedTypeArguments() == null) {
                throw new SchemaBuilderException(
                        "Don't know what to do with [" + fieldType + "] of kind [" + fieldType.kind() +
                                "] as parent object reference is missing or incomplete: " + parentObjectReference);
            }

            LOG.debug("Type variable: " + fieldType.asTypeVariable().name() + " identifier: " +
                              fieldType.asTypeVariable().identifier());

            Reference ret = parentObjectReference.getParametrizedTypeArguments()
                                                 .get(fieldType.asTypeVariable().identifier());

            if (ret == null) {
                throw new SchemaBuilderException(
                        "Don't know what to do with [" + fieldType + "] of kind [" + fieldType.kind() +
                                "] as parent object reference doesn't contain necessary info: " +
                                parentObjectReference);
            }

            return ret;
        } else {
            throw new SchemaBuilderException(
                    "Don't know what to do with [" + fieldType + "] of kind [" + fieldType.kind() + "]");
        }
    }

    public ParameterizedType findParametrizedParentType(ClassInfo classInfo) {
        if (classInfo != null && classInfo.superClassType() != null && !Classes.isEnum(classInfo)) {
            if (classInfo.superClassType().kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
                return classInfo.superClassType().asParameterizedType();
            }
            return findParametrizedParentType(ScanningContext.getIndex().getClassByName(classInfo.superName()));
        }
        return null;
    }

    private void collectTypeVariables(List<TypeVariable> tvl, ClassInfo classInfo) {
        if (classInfo == null)
            return;
        if (classInfo.typeParameters() != null) {
            tvl.addAll(classInfo.typeParameters());
        }
        if (classInfo.superClassType() != null) {
            collectTypeVariables(tvl, ScanningContext.getIndex().getClassByName(classInfo.superName()));
        }
    }

    public Map<String, Reference> collectParametrizedTypes(ClassInfo classInfo,
                                                           List<? extends Type> parametrizedTypeArguments,
                                                           Direction direction, Reference parentObjectReference) {
        Map<String, Reference> parametrizedTypeArgumentsReferences = null;
        if (parametrizedTypeArguments != null) {
            List<TypeVariable> tvl = new ArrayList<>();
            collectTypeVariables(tvl, classInfo);
            parametrizedTypeArgumentsReferences = new LinkedHashMap<>();
            int i = 0;
            for (Type pat : parametrizedTypeArguments) {
                if (i >= tvl.size()) {
                    throw new SchemaBuilderException(
                            "List of type variables is not correct for class " + classInfo + " and generics argument " +
                                    pat);
                } else {
                    parametrizedTypeArgumentsReferences.put(tvl.get(i++).identifier(),
                                                            getReference(direction, pat, null, null,
                                                                         parentObjectReference));
                }
            }
        }
        return parametrizedTypeArgumentsReferences;
    }

    public Reference createReference(Direction direction, ClassInfo classInfo, boolean createAdapedToType,
                                     boolean createAdapedWithType, Reference parentObjectReference,
                                     Map<String, Reference> parametrizedTypeArgumentsReferences,
                                     boolean addParametrizedTypeNameExtension) {
        // Get the initial reference type. It's either Type or Input depending on the direction. This might change as
        // we figure out this is actually an enum or interface
        ReferenceType referenceType = getCorrectReferenceType(direction);

        Annotations annotationsForClass = Annotations.getAnnotationsForClass(classInfo);


        // Now we should have the correct reference type.
        String className = classInfo.name().toString();

        String name = TypeNameHelper.getAnyTypeName(
                addParametrizedTypeNameExtension ? TypeNameHelper.createParametrizedTypeNameExtension(
                        parametrizedTypeArgumentsReferences) : null, referenceType, classInfo, annotationsForClass);

        Reference reference = new Reference(className, name, referenceType, parametrizedTypeArgumentsReferences,
                                            addParametrizedTypeNameExtension);

        // We ignore the field that is being adapted
        if (createAdapedToType && createAdapedWithType) {
            putIfAbsent(name, reference, referenceType);
        }
        return reference;
    }

    private Reference getNonIndexedReference(Direction direction, Type fieldType) {

        // If this is an unknown Wrapper, throw an exception
        if (fieldType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {

            if (direction.equals(Direction.IN)) {
                throw new IllegalArgumentException("Invalid parameter type [" + fieldType.name().toString() + "]");
            } else {
                throw new IllegalArgumentException("Invalid return type [" + fieldType.name().toString() + "]");
            }
        }

        LOG.warn("Class [" + fieldType.name() +
                         "] is not indexed in Jandex. Can not scan Object Type, might not be mapped correctly. Kind =" +
                         " [" + fieldType.kind() + "]");

        Reference r = new Reference();
        r.setClassName(fieldType.name().toString());
        r.setName(fieldType.name().local());
        r.setType(getCorrectReferenceType(direction));
        return r;
    }

}
