/*
 * Copyright (c) 2010-2020. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.iamcyw.tower.messaging;


import io.iamcyw.tower.common.IdentifierFactory;
import io.iamcyw.tower.messaging.unitofwork.CurrentUnitOfWork;

import java.util.Map;

/**
 * Generic implementation of a {@link Message} that contains the payload and metadata as unserialized values.
 * <p>
 * If a GenericMessage is created while a {@link io.iamcyw.tower.messaging.unitofwork.UnitOfWork} is active it copies
 * over the correlation data of the UnitOfWork to the created message.
 */
public class GenericMessage<T> extends AbstractMessage<T> {

    private static final long serialVersionUID = 7937214711724527316L;

    private final MetaData metaData;

    private final Class<T> payloadType;

    private final T payload;

    /**
     * Constructs a Message for the given {@code payload} using the correlation data of the current Unit of Work, if
     * present.
     *
     * @param payload The payload for the message
     */
    public GenericMessage(T payload) {
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
    public GenericMessage(T payload, Map<String, ?> metaData) {
        this((Class<T>) payload.getClass(), payload, metaData);
    }

    /**
     * Constructs a Message for the given {@code payload} and {@code meta data}. The given {@code metaData} is
     * merged with the MetaData from the correlation data of the current unit of work, if present.
     *
     * @param declaredPayloadType The declared type of message payload
     * @param payload             The payload for the message
     * @param metaData            The meta data for the message
     */
    public GenericMessage(Class<T> declaredPayloadType, T payload, Map<String, ?> metaData) {
        this(IdentifierFactory.getInstance().generateIdentifier(), declaredPayloadType, payload,
             CurrentUnitOfWork.correlationData().mergedWith(MetaData.from(metaData)));
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
    public GenericMessage(String identifier, T payload, Map<String, ?> metaData) {
        this(identifier, (Class<T>) payload.getClass(), payload, metaData);
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
    public GenericMessage(String identifier, Class<T> declaredPayloadType, T payload, Map<String, ?> metaData) {
        super(identifier);
        this.metaData = MetaData.from(metaData);
        this.payload = payload;
        this.payloadType = declaredPayloadType;
    }

    private GenericMessage(GenericMessage<T> original, MetaData metaData) {
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
    public T getPayload() {
        return payload;
    }

    @Override
    public Class<T> getPayloadType() {
        return payloadType;
    }

    @Override
    protected Message<T> withMetaData(MetaData metaData) {
        return new GenericMessage<>(this, metaData);
    }

}
