package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

public interface MessageHandlerInterceptor {

    <R> R filter(Message message, MessageHandlerInterceptorChain chain);

}
