package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.commandhandling.Registration;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;

import java.util.concurrent.CompletableFuture;

public interface QueryBus {

    <R> CompletableFuture<R> dispatch(Message message);

    Registration registerHandlerInterceptor(MessageHandlerInterceptor<?> messageHandlerInterceptor);

}
