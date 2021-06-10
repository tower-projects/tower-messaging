package io.iamcyw.tower.messaging.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaDataValue {

    /**
     * The key of the MetaData field to inject as method parameter.
     */
    String value();

    /**
     * Indicates whether the MetaData must be available in order for the Message handler method to be invoked. Defaults
     * to {@code false}, in which case {@code null} is injected as parameter.
     * <p/>
     * Note that if the annotated parameter is a primitive type, the required property will always be
     * {@code true}.
     */
    boolean required() default false;
}
