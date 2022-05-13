package io.iamcyw.tower.schema.creator;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.helper.DescriptionHelper;
import io.iamcyw.tower.schema.helper.Direction;
import io.iamcyw.tower.schema.model.Field;
import io.iamcyw.tower.schema.model.Wrapper;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

/**
 * Abstract creator
 */
public abstract class ModelCreator {

    protected final ReferenceCreator referenceCreator;

    public ModelCreator(ReferenceCreator referenceCreator) {
        this.referenceCreator = referenceCreator;
    }

    /**
     * The the return type.This is usually the method return type, but can also be adapted to something else
     *
     * @param methodInfo method
     * @return the return type
     */
    protected static Type getReturnType(MethodInfo methodInfo) {
        return methodInfo.returnType();
    }

    /**
     * The the return type.This is usually the method return type, but can also be adapted to something else
     *
     * @param fieldInfo
     * @return the return type
     */
    protected static Type getReturnType(FieldInfo fieldInfo) {
        return fieldInfo.type();
    }


    protected void populateField(Direction direction, Field field, Type type, Annotations annotations) {
        // Wrapper
        field.setWrapper(WrapperCreator.createWrapper(type).orElse(new Wrapper()));

        doPopulateField(direction, field, type, annotations);
    }

    protected void populateField(Direction direction, Field field, Type fieldType, Type methodType,
                                 Annotations annotations) {
        // Wrapper
        field.setWrapper(WrapperCreator.createWrapper(fieldType, methodType).orElse(null));

        doPopulateField(direction, field, methodType, annotations);
    }


    private void doPopulateField(Direction direction, Field field, Type type, Annotations annotations) {
        // Description
        DescriptionHelper.getDescriptionForField(annotations, type).ifPresent(field::setDescription);

        // NotNull
        field.setNotNull(true);
    }

}
