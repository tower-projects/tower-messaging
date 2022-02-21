package io.iamcyw.tower.messaging;

public class GenericMessage extends AbstractMessage {

    private final MetaData metaData;

    private final Object payload;

    private final Class<?> payloadType;

    public GenericMessage(String identifier, MetaData metaData, Object payload, Class<?> payloadType) {
        super(identifier);
        this.metaData = metaData;
        this.payload = payload;
        this.payloadType = payloadType;
    }

    public GenericMessage(MetaData metaData, Object payload, Class<?> payloadType) {
        this(payloadType.getSimpleName(), metaData, payload, payloadType);
    }

    public GenericMessage(GenericMessage original, MetaData metaData) {
        this(original.getIdentifier(), metaData, original.payload, original.payloadType);
    }

    public GenericMessage(Object payload) {
        this(new MetaData(), payload);
    }

    public GenericMessage(MetaData metaData, Object payload) {
        this(metaData, payload, payload.getClass());
    }


    @Override
    Message withMetaData(MetaData metaData) {
        return new GenericMessage(this, metaData);
    }

    @Override
    public MetaData getMetaData() {
        return this.metaData;
    }

    @Override
    public Object getPayload() {
        return this.payload;
    }

    @Override
    public Class<?> getPayloadType() {
        return this.payloadType;
    }

}
