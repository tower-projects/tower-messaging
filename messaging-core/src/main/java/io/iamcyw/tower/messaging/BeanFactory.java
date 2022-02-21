package io.iamcyw.tower.messaging;

public interface BeanFactory<I> {

    BeanInstance<I> createInstance();

    interface BeanInstance<I> extends AutoCloseable {
        I getInstance();

    }

}
