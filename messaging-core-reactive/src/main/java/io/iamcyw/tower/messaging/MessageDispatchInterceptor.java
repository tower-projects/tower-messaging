package io.iamcyw.tower.messaging;

public interface MessageDispatchInterceptor<T extends Message> {

    T handle(T message);

}
