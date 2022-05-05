package io.iamcyw.tower.messaging;

import java.lang.annotation.*;

@Repeatable(Parameters.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Parameter {

    String value();

    String parameter() default "";

}
