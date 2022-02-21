package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

public interface MessageDispatchInterceptor {

    Message handle(Message message);

}
