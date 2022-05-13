package io.iamcyw.tower.schema.helper;

import io.iamcyw.tower.schema.Annotations;
import org.jboss.jandex.Type;

import java.util.Optional;

/**
 * Helper to get the correct Description.
 * Basically looking for the @Description annotation.
 */
public class DescriptionHelper {

    private DescriptionHelper() {
    }

    /**
     * Get the Description on a field or argument
     *
     * @param annotations the annotations for that field/argument
     * @param type        the java type (some types have default values)
     * @return the optional description
     */
    public static Optional<String> getDescriptionForField(Annotations annotations, Type type) {

        if (annotations.containsKeyAndValidValue(Annotations.DESCRIPTION)) {
            return Optional.of(getGivenDescription(annotations));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the description on a class type
     *
     * @param annotations annotation on the class
     * @return the optional description
     */
    public static Optional<String> getDescriptionForType(Annotations annotations) {
        if (annotations.containsKeyAndValidValue(Annotations.DESCRIPTION)) {
            return Optional.of(getGivenDescription(annotations));
        }
        return Optional.empty();
    }


    private static String getGivenDescription(Annotations annotations) {
        return annotations.getAnnotationValue(Annotations.DESCRIPTION).asString();
    }

}
