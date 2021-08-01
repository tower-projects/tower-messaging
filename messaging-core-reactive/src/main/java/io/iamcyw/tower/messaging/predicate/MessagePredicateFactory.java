package io.iamcyw.tower.messaging.predicate;

import io.iamcyw.tower.messaging.Message;

import java.lang.reflect.Method;

public class MessagePredicateFactory {

    public static <T extends Message<?>> MessagePredicate<T> get(Method method) {
        return null;
    }

}
