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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReferenceCreator {

    private static ReferenceType getCorrectReferenceType(Direction direction) {
        if (direction.equals(Direction.IN)) {
            return ReferenceType.INPUT;
        } else {
            return ReferenceType.TYPE;
        }
    }

    public Reference createReferenceForOperationField(Type fieldType, Annotations annotationsForMethod) {
        return getReference(Direction.OUT, null, fieldType, annotationsForMethod);
    }

    public Reference createReferenceForSourceArgument(Type argumentType, Annotations annotationsForThisArgument) {
        return getReference(Direction.OUT, null, argumentType, annotationsForThisArgument);
    }

    private Reference getReference(Direction direction, Type fieldType, Type methodType, Annotations annotations) {
        return getReference(direction, fieldType, methodType, annotations, null);
    }

    private Reference getReference(Direction direction, Type fieldType, Type methodType, Annotations annotations,
                                   Reference parentObjectReference) {
        // In some case, like operations and interfaces, there is no fieldType
        if (fieldType == null) {
            fieldType = methodType;
        }

        String fieldTypeName = fieldType.name().toString();

        if (fieldType.kind().equals(Type.Kind.ARRAY)) {
            // Java Array
            Type typeInArray = fieldType.asArrayType().component();
            Type typeInMethodArray = methodType.asArrayType().component();
            return getReference(direction, typeInArray, typeInMethodArray, annotations, parentObjectReference);
        } else if (fieldType.kind().equals(Type.Kind.VOID)) {
            // Java void

            return getNonIndexedReference(direction, fieldType);
        } else if (Classes.isCollection(fieldType) || Classes.isUnwrappedType(fieldType)) {
            // Collections and unwrapped types
            Type typeInCollection = fieldType.asParameterizedType().arguments().get(0);
            Type typeInMethodCollection = methodType.asParameterizedType().arguments().get(0);
            return getReference(direction, typeInCollection, typeInMethodCollection, annotations,
                                parentObjectReference);
        } else if (Classes.isMap(fieldType)) {
            ParameterizedType parameterizedFieldType = fieldType.asParameterizedType();
            List<Type> fieldArguments = parameterizedFieldType.arguments();
            ParameterizedType entryType = ParameterizedType.create(Classes.ENTRY, fieldArguments.toArray(new Type[]{}),
                                                                   null);
            return getReference(direction, entryType, entryType, annotations, parentObjectReference);
        } else if (fieldType.kind().equals(Type.Kind.PRIMITIVE)) {
            return getNonIndexedReference(direction, fieldType);
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

                // boolean shouldCreateAdapedToType = AdaptToHelper.shouldCreateTypeInSchema(annotations);
                // boolean shouldCreateAdapedWithType = AdaptWithHelper.shouldCreateTypeInSchema(annotations);
                return createReference(direction, classInfo, parentObjectReference, parametrizedTypeArgumentsReferences,
                                       false);
            } else {
                return getNonIndexedReference(direction, fieldType);
            }
        } else {
            throw new SchemaBuilderException(
                    "Don't know what to do with [" + fieldType + "] of kind [" + fieldType.kind() + "]");
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

    public ParameterizedType findParametrizedParentType(ClassInfo classInfo) {
        if (classInfo != null && classInfo.superClassType() != null && !Classes.isEnum(classInfo)) {
            if (classInfo.superClassType().kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
                return classInfo.superClassType().asParameterizedType();
            }
            return findParametrizedParentType(ScanningContext.getIndex().getClassByName(classInfo.superName()));
        }
        return null;
    }

    private Reference createReference(Direction direction, ClassInfo classInfo, Reference parentObjectReference,
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

        // LOG.warn("Class [" + fieldType.name()
        //                  + "] is not indexed in Jandex. Can not scan Object Type, might not be mapped correctly.
        //                  Kind = ["
        //                  + fieldType.kind() + "]");

        Reference r = new Reference();
        r.setClassName(fieldType.name().toString());
        r.setName(fieldType.name().local());

        // boolean isNumber = Classes.isNumberLikeTypeOrContainedIn(fieldType);
        // boolean isDate = Classes.isDateLikeTypeOrContainedIn(fieldType);
        // if (isNumber || isDate) {
        //     r.setType(ReferenceType.SCALAR);
        // } else if (direction.equals(Direction.IN)) {
        //     r.setType(ReferenceType.INPUT);
        // } else {
        //     r.setType(ReferenceType.TYPE);
        // }
        return r;
    }

}
