package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;

public interface CommandBus {

    <R> R dispatch(Message message);

    Registration registerHandlerInterceptor(MessageHandlerInterceptor messageHandlerInterceptor);

}
