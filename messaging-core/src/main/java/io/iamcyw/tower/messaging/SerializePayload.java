package io.iamcyw.tower.messaging;

@FunctionalInterface
public interface SerializePayload {

    <T> T serialize(Class<T> serializeClass);

}
