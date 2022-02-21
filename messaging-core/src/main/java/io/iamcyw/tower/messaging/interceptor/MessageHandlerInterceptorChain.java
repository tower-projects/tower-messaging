package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

@FunctionalInterface
public interface MessageHandlerInterceptorChain {

    <R> R filter(Message message);

}
