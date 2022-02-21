package io.iamcyw.tower.messaging.handle.resolve;

import io.iamcyw.tower.messaging.Message;

public interface ParameterResolver<T> {

    T resolveParameterValue(Message message);

    boolean matches(Message message);

}
