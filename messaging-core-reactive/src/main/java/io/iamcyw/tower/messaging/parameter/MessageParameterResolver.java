package io.iamcyw.tower.messaging.parameter;

import io.iamcyw.tower.messaging.Message;

public class MessageParameterResolver implements ParameterResolver {

    private final Class<?> parameterType;

    public MessageParameterResolver(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public Object resolveParameterValue(Message message) {
        return message;
    }

    @Override
    public boolean matches(Message message) {
        return parameterType.isInstance(message);
    }

}
