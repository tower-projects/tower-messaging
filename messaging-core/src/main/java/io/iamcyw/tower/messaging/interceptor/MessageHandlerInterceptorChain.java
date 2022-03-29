package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface MessageHandlerInterceptorChain<R> {

    CompletableFuture<R> filter(Message message);

}
