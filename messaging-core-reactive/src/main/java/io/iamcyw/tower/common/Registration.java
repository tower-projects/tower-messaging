package io.iamcyw.tower.common;

/**
 * Interface that provides a mechanism to cancel a registration.
 */
@FunctionalInterface
public interface Registration extends AutoCloseable {

    /**
     * Cancels this Registration. By default this simply calls {@link #cancel()}.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    default void close() {
        cancel();
    }

    /**
     * Cancels this Registration. If the Registration was already cancelled, no action is taken.
     *
     * @return {@code true} if this handler is successfully unregistered, {@code false} if this handler
     * was not currently registered.
     */
    boolean cancel();

}
