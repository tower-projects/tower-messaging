package io.iamcyw.tower.queryhandling;


import io.iamcyw.tower.messaging.Message;

import java.util.Map;

/**
 * Message type that carries a Query: a request for information. Besides a payload, Query Messages also carry the
 * expected response type. This is the type of result expected by the caller.
 * <p>
 * Handlers should only answer a query if they can respond with the appropriate response type.
 */
public interface QueryMessage extends Message {

    /**
     * Extracts the {@code queryName} from the given {@code payloadOrMessage}, with three possible outcomes:
     * <ul>
     * <li>The {@code payloadOrMessage} is an instance of {@link QueryMessage} - {@link QueryMessage#getQueryName()}
     * is returned.</li>
     * <li>The {@code payloadOrMessage} is an instance of {@link Message} - the name of
     * {@link Message#getPayloadType()} is returned.</li>
     * <li>The {@code payloadOrMessage} is the query payload - {@link Class#getName()} is returned.</li>
     * </ul>
     *
     * @param payloadOrMessage the object to base the {@code queryName} on
     * @return the {@link QueryMessage#getQueryName()}, the name of {@link Message#getPayloadType()} or the result of
     * {@link Class#getName()}, depending on the type of the {@code payloadOrMessage}
     */
    static String queryName(Object payloadOrMessage) {
        if (payloadOrMessage instanceof QueryMessage) {
            return ((QueryMessage) payloadOrMessage).getQueryName();
        } else if (payloadOrMessage instanceof Message) {
            return ((Message) payloadOrMessage).getPayloadType().getName();
        }
        return payloadOrMessage.getClass().getName();
    }

    /**
     * Returns the name identifying the query to be executed.
     *
     * @return the name identifying the query to be executed.
     */
    String getQueryName();

    /**
     * Returns a copy of this QueryMessage with the given {@code metaData}. The payload remains unchanged.
     *
     * @param metaData The new MetaData for the QueryMessage
     * @return a copy of this message with the given MetaData
     */
    QueryMessage withMetaData(Map<String, ?> metaData);

    /**
     * Returns a copy of this QueryMessage with its MetaData merged with given {@code metaData}. The payload remains
     * unchanged.
     *
     * @param additionalMetaData The MetaData to merge into the QueryMessage
     * @return a copy of this message with the given additional MetaData
     */
    QueryMessage andMetaData(Map<String, ?> additionalMetaData);

}
