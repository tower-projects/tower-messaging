package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;

import java.util.concurrent.CompletableFuture;

public interface CommandBus {

    <R> CompletableFuture<R> dispatch(Message message);

    Registration registerHandlerInterceptor(MessageHandlerInterceptor<?> messageHandlerInterceptor);

}
