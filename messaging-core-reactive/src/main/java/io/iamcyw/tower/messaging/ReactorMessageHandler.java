package io.iamcyw.tower.messaging;

import io.smallrye.mutiny.Multi;

public interface ReactorMessageHandler<T extends Message<?>> {

    String getCommandName();

    <R> Multi<R> handle(T message);

    boolean canHandle(T message);

}