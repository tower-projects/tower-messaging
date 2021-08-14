package io.iamcyw.tower.messaging.parameter;

import io.iamcyw.tower.messaging.Message;

public class AnnotatedMetaDataParameterResolver implements ParameterResolver<Object> {

    private final String value;

    private final boolean required;

    private final Class parameterType;

    public AnnotatedMetaDataParameterResolver(String value, boolean required, Class parameterType) {
        this.value = value;
        this.required = required;
        this.parameterType = parameterType;
    }

    @Override
    public Object resolveParameterValue(Message message) {
        return message.getMetaData().get(value);
    }

    @Override
    public boolean matches(Message message) {
        return !(parameterType.isPrimitive() || required) || (message.getMetaData().containsKey(value) &&
                parameterType.isInstance(message.getMetaData().get(value)));
    }

}
