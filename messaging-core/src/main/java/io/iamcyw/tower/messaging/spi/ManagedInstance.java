package io.iamcyw.tower.messaging.spi;

public interface ManagedInstance<T> {

    T get();

    default void destroyIfNecessary() {
        // nothing
    }

}
