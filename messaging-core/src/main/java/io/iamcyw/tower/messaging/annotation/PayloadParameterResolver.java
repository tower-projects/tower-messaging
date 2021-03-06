/*
 * Copyright (c) 2010-2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.iamcyw.tower.messaging.annotation;


import io.iamcyw.tower.messaging.Message;

/**
 * Implementation of a {@link ParameterResolver} that resolves the Message payload as parameter in a handler method.
 */
public class PayloadParameterResolver<T> implements ParameterResolver<T> {

    private final Class<T> payloadType;

    /**
     * Initializes a new {@link PayloadParameterResolver} for a method parameter of given {@code payloadType}. This
     * parameter resolver matches with a message if the payload of the message is assignable to the given {@code
     * payloadType}.
     *
     * @param payloadType the parameter type
     */
    public PayloadParameterResolver(Class<T> payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public T resolveParameterValue(Message message) {
        return (T) message.getPayload();
    }

    @Override
    public boolean matches(Message message) {
        return message.getPayloadType() != null && payloadType.isAssignableFrom(message.getPayloadType());
    }

    @Override
    public Class<T> supportedPayloadType() {
        return payloadType;
    }

}
