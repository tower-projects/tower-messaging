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

import java.lang.reflect.Executable;
import java.util.Optional;

import static io.iamcyw.tower.common.annotation.AnnotationUtils.findAnnotationAttributes;


public class AnnotatedMessageHandlingMemberDefinition implements HandlerDefinition {
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<MessageHandlingMember<T>> createHandler(Class<T> declaringType, Executable executable,
                                                                ParameterResolverFactory parameterResolverFactory) {
        return findAnnotationAttributes(executable, MessageHandler.class)
                .map(attr -> new AnnotatedMessageHandlingMember<>(executable, (Class<? extends Message>) attr
                        .getOrDefault("messageType", Message.class), (Class<? extends Message>) attr
                        .getOrDefault("payloadType", Object.class), parameterResolverFactory));
    }

}
