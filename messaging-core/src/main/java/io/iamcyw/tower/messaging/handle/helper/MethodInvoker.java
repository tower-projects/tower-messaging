package io.iamcyw.tower.messaging.handle.helper;

public interface MethodInvoker {

    <R> R invoke(Object instance, Object[] args);

}
