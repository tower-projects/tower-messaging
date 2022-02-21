package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.handle.MessageHandle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class MetaData {
    private final Map<String, Object> value;

    public MetaData() {
        value = new HashMap<>();
    }

    public MetaData(Map<String, Object> value) {
        this.value = value;
    }

    public static MetaData from(Map<String, Object> metaData) {
        return new MetaData(metaData);
    }

    public MetaData mergedWith(Map<String, Object> metaData) {
        metaData.putAll(value);
        return new MetaData(metaData);
    }

    public Object get(String key) {
        return value.get(key);
    }

    public String[] getPredicateParameter() {
        return (String[]) value.get("PREDICATE_PARAMETER");
    }

    public void setPredicateParameter(String[] parameter) {
        value.put("PREDICATE_PARAMETER", parameter);
    }

    public Stream<MessageHandle> getMessageHandlers() {
        return (Stream<MessageHandle>) value.get("MESSAGE_HANDLERS");
    }

    public void setMessageHandlers(Stream<MessageHandle> messageHandlers) {
        value.put("MESSAGE_HANDLERS", messageHandlers);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MetaData metaData = (MetaData) o;

        return Objects.equals(value, metaData.value);
    }

    public boolean hasMetaData(String key) {
        return this.value.containsKey(key);
    }

}
