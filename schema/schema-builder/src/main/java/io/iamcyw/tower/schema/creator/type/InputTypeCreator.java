package io.iamcyw.tower.schema.creator.type;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.Classes;
import io.iamcyw.tower.schema.helper.DescriptionHelper;
import io.iamcyw.tower.schema.helper.TypeNameHelper;
import io.iamcyw.tower.schema.model.InputType;
import io.iamcyw.tower.schema.model.Reference;
import io.iamcyw.tower.schema.model.ReferenceType;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.logging.Logger;

import java.lang.reflect.Modifier;

public class InputTypeCreator implements Creator<InputType> {

    private static final Logger LOG = Logger.getLogger(InputTypeCreator.class.getName());

    public boolean hasUseableConstructor(ClassInfo classInfo) {
        MethodInfo constructor = findCreator(classInfo);
        return constructor != null;
    }

    public MethodInfo findCreator(ClassInfo classInfo) {
        if (Classes.RECORD.equals(classInfo.superName())) {
            // records should always have a canonical constructor
            // the creator will be picked by the JSONB impl at runtime anyway, so
            // just make sure we can find a public constructor and move on
            for (MethodInfo constructor : classInfo.constructors()) {
                if (!Modifier.isPublic(constructor.flags()))
                    continue;
                return constructor;
            }
            return null;
        }

        for (final MethodInfo constructor : classInfo.constructors()) {
            if (!Modifier.isPublic(constructor.flags()))
                continue;
            if (constructor.parameters().isEmpty()) {
                return constructor;
            }
            if (constructor.hasAnnotation(Annotations.JAKARTA_JSONB_CREATOR) ||
                    constructor.hasAnnotation(Annotations.JAVAX_JSONB_CREATOR) ||
                    constructor.hasAnnotation(Annotations.JACKSON_CREATOR)) {
                return constructor;
            }
        }

        for (final MethodInfo factoryMethod : classInfo.methods()) {
            if (!Modifier.isStatic(factoryMethod.flags()))
                continue;
            if (!Modifier.isPublic(factoryMethod.flags()))
                continue;

            if (factoryMethod.hasAnnotation(Annotations.JAKARTA_JSONB_CREATOR) ||
                    factoryMethod.hasAnnotation(Annotations.JAVAX_JSONB_CREATOR) ||
                    factoryMethod.hasAnnotation(Annotations.JACKSON_CREATOR)) {
                return factoryMethod;
            }
        }

        return null;
    }

    @Override
    public InputType create(ClassInfo classInfo, Reference reference) {
        if (!hasUseableConstructor(classInfo)) {
            throw new IllegalArgumentException("Class " + classInfo.name().toString() +
                                                       " is used as input, but does neither have a public default " +
                                                       "constructor nor a JsonbCreator method");
        }

        LOG.debug("Creating Input from " + classInfo.name().toString());

        Annotations annotations = Annotations.getAnnotationsForClass(classInfo);

        // Name
        String name = TypeNameHelper.getAnyTypeName(reference, ReferenceType.INPUT, classInfo, annotations);

        // Description
        String description = DescriptionHelper.getDescriptionForType(annotations).orElse(null);

        InputType inputType = new InputType(classInfo.name().toString(), name, description);

        return inputType;
    }

}
