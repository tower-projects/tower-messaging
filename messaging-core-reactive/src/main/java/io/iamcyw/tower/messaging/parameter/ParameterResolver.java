package io.iamcyw.tower.messaging.parameter;


import io.iamcyw.tower.messaging.Message;

/**
 * Interface for a mechanism that resolves handler method parameter values from a given {@link Message}.
 *
 * @param <T> The type of parameter returned by this resolver
 * @author Allard Buijze
 * @since 2.0
 */
public interface ParameterResolver<T> {

    /**
     * Resolves the parameter value to use for the given {@code message}, or {@code null} if no suitable
     * parameter value can be resolved.
     *
     * @param message The message to resolve the value from
     * @return the parameter value for the handler
     */
    T resolveParameterValue(Message message);

    /**
     * Indicates whether this resolver is capable of providing a value for the given {@code message}.
     *
     * @param message The message to evaluate
     * @return {@code true} if this resolver can provide a value for the message, otherwise {@code false}
     */
    boolean matches(Message message);

    /**
     * Returns the class of the payload that is supported by this resolver. Defaults to the {@link Object} class
     * indicating that the payload type is irrelevant for this resolver.
     *
     * @return The class of the payload that is supported by this resolver
     */
    default Class<T> supportedPayloadType() {
        return (Class<T>) Object.class;
    }

}
