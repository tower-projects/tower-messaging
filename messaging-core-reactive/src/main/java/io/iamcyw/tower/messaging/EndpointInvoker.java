package io.iamcyw.tower.messaging;

public interface EndpointInvoker {

    Object invoke(Object instance, Object[] args);

}
