package io.iamcyw.tower.messaging;

import io.smallrye.mutiny.Multi;

public interface ReactorMessageHandler<T extends Message<?>> {

    boolean canHandle(T message);

    <R> Multi<R> handle(T message);

    /**
     * Returns the instance type that this handler delegates to.
     *
     * @return Returns the instance type that this handler delegates to
     */
    default Class<?> getTargetType() {
        return getClass();
    }

    /**
     * Indicates whether this handler can handle messages of given type
     *
     * @param payloadType The payloadType to verify
     * @return {@code true} if this handler can handle the payloadType, otherwise {@code false}
     */
    default boolean canHandleType(Class<?> payloadType) {
        return true;
    }

}