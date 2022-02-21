package io.iamcyw.tower.messaging;

import io.iamcyw.tower.utils.lang.NonNull;

import java.util.Map;

public abstract class AbstractMessage implements Message {

    @NonNull
    private final String identifier;

    protected AbstractMessage(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Message withMetaData(@NonNull Map<String, Object> metaData) {
        if (getMetaData().equals(metaData)) {
            return this;
        } else {
            return withMetaData(MetaData.from(metaData));
        }
    }

    @Override
    public Message andMetaData(@NonNull Map<String, Object> metaData) {
        if (metaData.isEmpty()) {
            return this;
        } else {
            return withMetaData(getMetaData().mergedWith(metaData));
        }
    }

    abstract Message withMetaData(MetaData metaData);

}
