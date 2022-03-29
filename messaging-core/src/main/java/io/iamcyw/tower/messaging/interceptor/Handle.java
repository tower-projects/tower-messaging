package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

public interface Handle<R> {

    CompletableFuture<R> handle(Message message);

}
