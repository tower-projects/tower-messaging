package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.handle.Identifier;
import io.iamcyw.tower.messaging.responsetype.ResponseType;
import io.iamcyw.tower.messaging.responsetype.ResponseTypes;
import io.iamcyw.tower.schema.model.OperationType;
import io.iamcyw.tower.schema.model.WrapperType;

import java.util.Map;

public class GenericMessage<R> implements Message<R> {

    private final Identifier identifier;

    private final MetaData metaData;

    private final Object payload;

    private final ResponseType responseType;

    private final OperationType operationType;

    public GenericMessage(Object payload, MetaData metaData, ResponseType responseType, OperationType operationType) {
        this.identifier = new Identifier(payload.getClass().getName(), responseType.name(),
                                         responseType.responseMessagePayloadWrapperType());
        this.metaData = metaData;
        this.payload = payload;
        this.responseType = responseType;
        this.operationType = operationType;
    }

    public GenericMessage(Object payload, ResponseType responseType, OperationType operationType) {
        this.identifier = new Identifier(payload.getClass().getName(), responseType.name(),
                                         responseType.responseMessagePayloadWrapperType());
        this.metaData = new MetaData();
        this.payload = payload;
        this.responseType = responseType;
        this.operationType = operationType;
    }

    public GenericMessage(Object payload, String fieldName, WrapperType wrapperType, OperationType operationType) {
        this.identifier = new Identifier(payload.getClass().getName(), fieldName, wrapperType);
        this.metaData = new MetaData();
        this.payload = payload;
        this.operationType = operationType;
        this.responseType = ResponseTypes.instanceOf(fieldName, wrapperType);
    }

    public GenericMessage(GenericMessage original, MetaData metaData) {
        this(original.payload, metaData, original.responseType, original.operationType);
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

    Message<R> withMetaData(MetaData metaData) {
        return new GenericMessage(this, metaData);
    }

    public ResponseType<R> getResponseType() {
        return responseType;
    }

}
