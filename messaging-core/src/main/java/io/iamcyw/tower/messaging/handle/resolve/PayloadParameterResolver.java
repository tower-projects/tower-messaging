package io.iamcyw.tower.messaging.handle.resolve;

import io.iamcyw.tower.messaging.Message;

public class PayloadParameterResolver<T> implements ParameterResolver<T> {
    private Class<T> payloadType;

    public PayloadParameterResolver() {
        this(null);
    }

    public PayloadParameterResolver(Class<T> payloadType) {
        this.payloadType = payloadType;
    }

    public Class<T> getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(Class<T> payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public T resolveParameterValue(Message message) {
        return payloadType.cast(message.getPayload());
    }

    @Override
    public boolean matches(Message message) {
        return message.getPayloadType() == payloadType;
    }

}
