package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

public interface Handle<R> {

    R handle(Message message);

}
