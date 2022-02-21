package io.iamcyw.tower.messaging.handle;

public interface MethodInvoker {

    <R> R invoke(Object instance, Object[] args);

}
