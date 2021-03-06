/*
 * Copyright (c) 2010-2018. Axon Framework
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

package io.iamcyw.tower.queryhandling;


import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MessageDecorator;
import io.iamcyw.tower.messaging.MetaData;

import java.util.Map;

/**
 * Generic {@link SubscriptionQueryUpdateMessage} which holds incremental update of an subscription query.
 *
 * @param <U> type of incremental update
 */
public class GenericSubscriptionQueryUpdateMessage<U> extends MessageDecorator<U> implements SubscriptionQueryUpdateMessage<U> {

    private static final long serialVersionUID = 5872479410321475147L;

    /**
     * Initializes {@link GenericSubscriptionQueryUpdateMessage} with incremental update.
     *
     * @param payload payload of the message which represent incremental update
     */
    public GenericSubscriptionQueryUpdateMessage(U payload) {
        this(new GenericMessage<>(payload, MetaData.emptyInstance()));
    }

    /**
     * Initializes a new decorator with given {@code delegate} message. The decorator delegates to the delegate for
     * the message's payload, metadata and identifier.
     *
     * @param delegate the message delegate
     */
    protected GenericSubscriptionQueryUpdateMessage(Message<U> delegate) {
        super(delegate);
    }

    /**
     * Creates {@link GenericSubscriptionQueryUpdateMessage} from provided object which represents incremental update.
     *
     * @param o   incremental update
     * @param <T> type of the {@link GenericSubscriptionQueryUpdateMessage}
     * @return created message
     */
    @SuppressWarnings("unchecked")
    public static <T> GenericSubscriptionQueryUpdateMessage<T> from(Object o) {
        return new GenericSubscriptionQueryUpdateMessage<>((T) o);
    }

    @Override
    public SubscriptionQueryUpdateMessage<U> withMetaData(Map<String, ?> metaData) {
        return new GenericSubscriptionQueryUpdateMessage<>(getDelegate().withMetaData(metaData));
    }

    @Override
    public SubscriptionQueryUpdateMessage<U> andMetaData(Map<String, ?> metaData) {
        return new GenericSubscriptionQueryUpdateMessage<>(getDelegate().andMetaData(metaData));
    }

    @Override
    protected String describeType() {
        return "GenericSubscriptionQueryUpdateMessage";
    }

}
