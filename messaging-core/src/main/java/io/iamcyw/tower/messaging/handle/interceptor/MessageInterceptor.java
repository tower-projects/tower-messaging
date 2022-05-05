package io.iamcyw.tower.messaging.handle.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

public interface MessageInterceptor<R> {

    CompletableFuture<R> filter(Message message, InterceptorChain<?> chain);

}
