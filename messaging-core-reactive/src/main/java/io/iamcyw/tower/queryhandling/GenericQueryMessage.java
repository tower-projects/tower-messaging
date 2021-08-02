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
 * Generic implementation of the QueryMessage. Unless explicitly provided, it assumes the {@code queryName} of the
 * message is the fully qualified class name of the message's payload.
 */
public class GenericQueryMessage extends MessageDecorator implements QueryMessage {

    private static final long serialVersionUID = -3908412412867063631L;

    private final String queryName;

    /**
     * Initializes the message with the given {@code payload} and expected {@code responseType}. The query name is set
     * to the fully qualified class name of the {@code payload}.
     *
     * @param payload The payload expressing the query
     */
    public GenericQueryMessage(Object payload) {
        this(payload, payload.getClass().getName());
    }

    /**
     * Initializes the message with the given {@code payload}, {@code queryName} and expected {@code responseType}.
     *
     * @param payload The payload expressing the query
     */
    public GenericQueryMessage(Object payload, String queryName) {
        this(new GenericMessage(payload, MetaData.emptyInstance()), queryName);
    }

    /**
     * Initialize the Query Message, using given {@code delegate} as the carrier of payload and metadata and given
     * {@code queryName} and expecting the given {@code responseType}.
     *
     * @param delegate  The message containing the payload and meta data for this message
     * @param queryName The name identifying the query to execute
     */
    public GenericQueryMessage(Message delegate, String queryName) {
        super(delegate);
        this.queryName = queryName;
    }

    @Override
    public String getQueryName() {
        return queryName;
    }

    @Override
    public QueryMessage withMetaData(Map<String, ?> metaData) {
        return new GenericQueryMessage(getDelegate().withMetaData(metaData), queryName);
    }

    @Override
    public QueryMessage andMetaData(Map<String, ?> metaData) {
        return new GenericQueryMessage(getDelegate().andMetaData(metaData), queryName);
    }

    @Override
    protected void describeTo(StringBuilder stringBuilder) {
        super.describeTo(stringBuilder);
        stringBuilder.append(", queryName='").append(getQueryName()).append('\'').append(", expectedResponseType='")
                     .append('\'');
    }

    @Override
    protected String describeType() {
        return "GenericQueryMessage";
    }

}
