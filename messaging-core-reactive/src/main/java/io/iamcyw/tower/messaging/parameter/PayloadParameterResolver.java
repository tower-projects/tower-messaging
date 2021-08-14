package io.iamcyw.tower.messaging.parameter;


import io.iamcyw.tower.messaging.Message;

/**
 * Implementation of a {@link ParameterResolver} that resolves the Message payload as parameter in a handler method.
 */
public class PayloadParameterResolver<T> implements ParameterResolver<T> {

    private final Class<T> payloadType;

    /**
     * Initializes a new {@link PayloadParameterResolver} for a method parameter of given {@code payloadType}. This
     * parameter resolver matches with a message if the payload of the message is assignable to the given {@code
     * payloadType}.
     *
     * @param payloadType the parameter type
     */
    public PayloadParameterResolver(Class<T> payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public T resolveParameterValue(Message message) {
        return (T) message.getPayload();
    }

    @Override
    public boolean matches(Message message) {
        return message.getPayloadType() != null && payloadType.isAssignableFrom(message.getPayloadType());
    }

    @Override
    public Class<T> supportedPayloadType() {
        return payloadType;
    }

}
