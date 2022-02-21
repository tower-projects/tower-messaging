package io.iamcyw.tower.messaging.handle.resolve;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.utils.lang.NonNull;

public class MetaDataValueParameterResolver<T> implements ParameterResolver<T> {
    @NonNull
    private String metaKey;

    @NonNull
    private Class<T> dataType;

    public MetaDataValueParameterResolver(String metaKey, Class<T> dataType) {
        this.metaKey = metaKey;
        this.dataType = dataType;
    }

    public MetaDataValueParameterResolver() {
        this.metaKey = "";
        this.dataType = (Class<T>) Object.class;
    }

    @NonNull
    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(@NonNull String metaKey) {
        this.metaKey = metaKey;
    }

    @NonNull
    public Class<T> getDataType() {
        return dataType;
    }

    public void setDataType(@NonNull Class<T> dataType) {
        this.dataType = dataType;
    }

    @Override
    public T resolveParameterValue(Message message) {
        return dataType.cast(message.getMetaData().get(metaKey));
    }

    @Override
    public boolean matches(Message message) {
        return message.getMetaData().hasMetaData(metaKey);
    }

}
