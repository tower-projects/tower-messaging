package io.iamcyw.tower.messaging.handle.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

public interface MessageInterceptor {

    default Long order() {
        return 0L;
    }

    <R> CompletableFuture<R> filter(Message<R> message, InterceptorChain chain);

}
