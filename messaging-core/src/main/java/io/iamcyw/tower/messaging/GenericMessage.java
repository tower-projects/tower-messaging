package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.handle.Identifier;
import io.iamcyw.tower.messaging.responsetype.ResponseType;
import io.iamcyw.tower.schema.model.OperationType;

import java.util.Map;

public class GenericMessage<R> implements Message<R> {

    private final Identifier identifier;

    private final MetaData metaData;

    private final Object payload;

    private final ResponseType responseType;

    private final OperationType operationType;

    public GenericMessage(Object payload, MetaData metaData, String command, ResponseType responseType,
                          OperationType operationType) {
        this(payload, new Identifier(command, responseType.name()), metaData, responseType, operationType);
    }

    public GenericMessage(Object payload, Identifier identifier, MetaData metaData, ResponseType responseType,
                          OperationType operationType) {
        this.identifier = identifier;
        this.metaData = metaData;
        this.payload = payload;
        this.operationType = operationType;
        this.responseType = responseType;
    }

    public GenericMessage(GenericMessage<R> original, MetaData metaData) {
        this(original.payload, original.identifier, metaData, original.responseType, original.operationType);
    }

    public GenericMessage(GenericMessage<R> original, Object payload) {
        this(payload, original.identifier, original.metaData, original.responseType, original.operationType);
    }

    public GenericMessage(Object payload, ResponseType responseType, OperationType operationType) {
        this(payload, new MetaData(), payload.getClass().getName(), responseType, operationType);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public Message<R> withMetaData(Map<String, Object> metaData) {
        if (getMetaData().equals(metaData)) {
            return this;
        } else {
            return withMetaData(MetaData.from(metaData));
        }
    }

    @Override
    public Message<R> andMetaData(Map<String, Object> metaData) {
        if (metaData.isEmpty()) {
            return this;
        } else {
            return withMetaData(getMetaData().mergedWith(metaData));
        }
    }

    @Override
    public Message<R> updatePayload(Object payload) {
        return new GenericMessage<R>(this, payload);
    }

    Message<R> withMetaData(MetaData metaData) {
        return new GenericMessage(this, metaData);
    }

    public ResponseType getResponseType() {
        return responseType;
    }

}
