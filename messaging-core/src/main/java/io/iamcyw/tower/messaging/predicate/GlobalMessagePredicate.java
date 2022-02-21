package io.iamcyw.tower.messaging.predicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@Repeatable(value = GlobalMessagePredicates.class)
@Target(ElementType.METHOD)
public @interface GlobalMessagePredicate {

    String value();

    String[] parameter();

}
