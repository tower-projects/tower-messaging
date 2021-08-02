package io.iamcyw.tower.messaging;

import java.io.Serializable;
import java.util.Map;

public interface Message extends Serializable {

    /**
     * Returns the identifier of this message. Two messages with the same identifiers should be interpreted as
     * different representations of the same conceptual message. In such case, the meta-data may be different for both
     * representations. The payload <em>may</em> be identical.
     *
     * @return the unique identifier of this message
     */
    String getIdentifier();

    /**
     * Returns the meta data for this event. This meta data is a collection of key-value pairs, where the key is a
     * String, and the value is a serializable object.
     *
     * @return the meta data for this event
     */
    MetaData getMetaData();

    /**
     * Returns the payload of this Event. The payload is the application-specific information.
     *
     * @return the payload of this Event
     */
    Object getPayload();

    /**
     * Returns the type of the payload.
     * <p/>
     * Is semantically equal to {@code getPayload().getClass()}, but allows implementations to optimize by using
     * lazy loading or deserialization.
     *
     * @return the type of payload.
     */
    Class<?> getPayloadType();

    /**
     * Returns a copy of this Message with the given {@code metaData}. The payload remains unchanged.
     * <p/>
     * While the implementation returned may be different than the implementation of {@code this}, implementations
     * must take special care in returning the same type of Message (e.g. EventMessage, DomainEventMessage) to prevent
     * errors further downstream.
     *
     * @param metaData The new MetaData for the Message
     * @return a copy of this message with the given MetaData
     */
    Message withMetaData(Map<String, ?> metaData);

    /**
     * Returns a copy of this Message with it MetaData merged with the given {@code metaData}. The payload
     * remains unchanged.
     *
     * @param metaData The MetaData to merge with
     * @return a copy of this message with the given MetaData
     */
    Message andMetaData(Map<String, ?> metaData);

}
