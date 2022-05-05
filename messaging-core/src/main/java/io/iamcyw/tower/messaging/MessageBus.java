package io.iamcyw.tower.messaging;

import java.util.concurrent.CompletableFuture;

public interface MessageBus {

    <R> CompletableFuture<R> dispatch(Message<R> message);

}
