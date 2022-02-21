package io.iamcyw.tower.messaging.predicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface MessagePredicates {

    MessagePredicate[] value();

}
