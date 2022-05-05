package io.iamcyw.tower.messaging.handle.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface InterceptorChain<R> {

    CompletableFuture<R> filter(Message message);

}
