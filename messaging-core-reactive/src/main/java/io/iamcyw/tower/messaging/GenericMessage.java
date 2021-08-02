package io.iamcyw.tower.messaging;

import java.util.Map;

public class GenericMessage extends AbstractMessage {

    private static final long serialVersionUID = 7937214711724527316L;

    private final MetaData metaData;

    private final Class<?> payloadType;

    private final Object payload;

    /**
     * Constructs a Message for the given {@code payload} using the correlation data of the current Unit of Work, if
     * present.
     *
     * @param payload The payload for the message
     */
    public GenericMessage(Object payload) {
        this(payload, MetaData.emptyInstance());
    }

    /**
     * Constructs a Message for the given {@code payload} and {@code meta data}. The given {@code metaData} is
     * merged with the MetaData from the correlation data of the current unit of work, if present.
     *
     * @param payload  The payload for the message
     * @param metaData The meta data for the message
     */
    @SuppressWarnings("unchecked")
    public GenericMessage(Object payload, Map<String, ?> metaData) {
        this(payload.getClass(), payload, metaData);
    }

    /**
     * Constructs a Message for the given {@code payload} and {@code meta data}. The given {@code metaData} is
     * merged with the MetaData from the correlation data of the current unit of work, if present.
     *
     * @param declaredPayloadType The declared type of message payload
     * @param payload             The payload for the message
     * @param metaData            The meta data for the message
     */
    public GenericMessage(Class<?> declaredPayloadType, Object payload, Map<String, ?> metaData) {
        this(null, declaredPayloadType, payload, metaData);
    }

    /**
     * Constructor to reconstruct a Message using existing data. Note that no correlation data
     * from a UnitOfWork is attached when using this constructor. If you're constructing a new
     * Message, use {@link #GenericMessage(Object, Map)} instead.
     *
     * @param identifier The identifier of the Message
     * @param payload    The payload of the message
     * @param metaData   The meta data of the message
     * @throws NullPointerException when the given {@code payload} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    public GenericMessage(String identifier, Object payload, Map<String, ?> metaData) {
        this(identifier, (Class<?>) payload.getClass(), payload, metaData);
    }

    /**
     * Constructor to reconstruct a Message using existing data. Note that no correlation data
     * from a UnitOfWork is attached when using this constructor. If you're constructing a new
     * Message, use {@link #GenericMessage(Object, Map)} instead
     *
     * @param identifier          The identifier of the Message
     * @param declaredPayloadType The declared type of message payload
     * @param payload             The payload for the message
     * @param metaData            The meta data for the message
     */
    public GenericMessage(String identifier, Class<?> declaredPayloadType, Object payload, Map<String, ?> metaData) {
        super(identifier);
        this.metaData = MetaData.from(metaData);
        this.payload = payload;
        this.payloadType = declaredPayloadType;
    }

    private GenericMessage(GenericMessage original, MetaData metaData) {
        super(original.getIdentifier());
        this.payload = original.getPayload();
        this.payloadType = original.getPayloadType();
        this.metaData = metaData;
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
    public Class<?> getPayloadType() {
        return payloadType;
    }

    @Override
    protected Message withMetaData(MetaData metaData) {
        return new GenericMessage(this, metaData);
    }

}
