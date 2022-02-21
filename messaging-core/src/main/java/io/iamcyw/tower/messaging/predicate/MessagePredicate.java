package io.iamcyw.tower.messaging.predicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Repeatable(value = MessagePredicates.class)
@Target(ElementType.METHOD)
public @interface MessagePredicate {
    String value();

    String[] parameter() default {};

}
