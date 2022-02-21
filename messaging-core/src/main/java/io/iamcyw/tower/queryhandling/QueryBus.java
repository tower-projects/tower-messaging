package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.commandhandling.Registration;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;

public interface QueryBus {

    <R> R dispatch(Message message);

    Registration registerHandlerInterceptor(MessageHandlerInterceptor messageHandlerInterceptor);

}
