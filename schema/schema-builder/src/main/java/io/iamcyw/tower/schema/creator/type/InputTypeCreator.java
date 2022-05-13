package io.iamcyw.tower.schema.creator.type;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.Classes;
import io.iamcyw.tower.schema.ScanningContext;
import io.iamcyw.tower.schema.creator.FieldCreator;
import io.iamcyw.tower.schema.helper.DescriptionHelper;
import io.iamcyw.tower.schema.helper.Direction;
import io.iamcyw.tower.schema.helper.MethodHelper;
import io.iamcyw.tower.schema.helper.TypeNameHelper;
import io.iamcyw.tower.schema.model.Field;
import io.iamcyw.tower.schema.model.InputType;
import io.iamcyw.tower.schema.model.Reference;
import io.iamcyw.tower.schema.model.ReferenceType;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.logging.Logger;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This creates an input type object.
 * <p>
 * The input object has fields that might reference other types
 * that should still be created.
 */
public class InputTypeCreator implements Creator<InputType> {
    private static final Logger LOG = Logger.getLogger(InputTypeCreator.class.getName());

    private static final String JAVA_DOT = "java.";

    private final FieldCreator fieldCreator;

    public InputTypeCreator(FieldCreator fieldCreator) {
        this.fieldCreator = fieldCreator;
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

        // Fields
        addFields(inputType, classInfo, reference);

        return inputType;
    }

    public boolean hasUseableConstructor(ClassInfo classInfo) {
        MethodInfo constructor = findCreator(classInfo);
        return constructor != null;
    }

    /**
     * Returns a constructor or factory method to create instances of this class.
     * <p>
     * Could either be the default constructor or any constructor or static method annotated with {@code @JsonbCreator}
     *
     * @param classInfo the class whose creator is to be found
     * @return the creator, null, if no public constructor or factory method is found
     */
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

    private void addFields(InputType inputType, ClassInfo classInfo, Reference reference) {
        // Fields
        List<MethodInfo> allMethods = new ArrayList<>();
        Map<String, FieldInfo> allFields = new HashMap<>();
        MethodInfo creator = findCreator(classInfo);

        // Find all methods and properties up the tree
        for (ClassInfo c = classInfo; c != null; c = ScanningContext.getIndex().getClassByName(c.superName())) {
            if (!c.toString().startsWith(JAVA_DOT)) { // Not java objects
                allMethods.addAll(c.methods());
                for (final FieldInfo fieldInfo : c.fields()) {
                    allFields.putIfAbsent(fieldInfo.name(), fieldInfo);
                }
            }
        }

        //Parameters of JsonbCreator
        for (short i = 0; i < creator.parameters().size(); i++) {
            String fieldName = creator.parameterName(i);
            FieldInfo fieldInfo = allFields.remove(fieldName);
            final Optional<Field> maybeField = fieldCreator.createFieldForParameter(creator, i, fieldInfo, reference);
            maybeField.ifPresent(inputType::addField);
            maybeField.ifPresent(inputType::addCreatorParameter);
        }

        for (MethodInfo methodInfo : allMethods) {
            if (MethodHelper.isPropertyMethod(Direction.IN, methodInfo)) {
                String fieldName = MethodHelper.getPropertyName(Direction.IN, methodInfo.name());
                FieldInfo fieldInfo = allFields.remove(fieldName);
                fieldCreator.createFieldForPojo(Direction.IN, fieldInfo, methodInfo, reference)
                            .ifPresent(inputType::addField);
            }
        }

        // See what fields are left (this is fields without methods)
        if (!allFields.isEmpty()) {
            for (FieldInfo fieldInfo : allFields.values()) {
                fieldCreator.createFieldForPojo(Direction.IN, fieldInfo, reference).ifPresent(inputType::addField);
            }
        }

    }

}