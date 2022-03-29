package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

public interface MessageHandlerInterceptor<R> {

    CompletableFuture<R> filter(Message message, MessageHandlerInterceptorChain<?> chain);

    Class<R> type();

    default boolean match(Message message) {
        return true;
    }

}
