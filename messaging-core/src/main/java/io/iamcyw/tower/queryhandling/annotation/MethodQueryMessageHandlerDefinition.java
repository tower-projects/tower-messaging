/*
 * Copyright (c) 2010-2019. Axon Framework
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

package io.iamcyw.tower.queryhandling.annotation;


import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.annotation.HandlerEnhancerDefinition;
import io.iamcyw.tower.messaging.annotation.MessageHandlingMember;
import io.iamcyw.tower.messaging.annotation.UnsupportedHandlerException;
import io.iamcyw.tower.messaging.annotation.WrappedMessageHandlingMember;
import io.iamcyw.tower.queryhandling.QueryHandler;
import io.iamcyw.tower.queryhandling.QueryMessage;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import static io.iamcyw.tower.utils.ReflectionUtils.resolvePrimitiveWrapperTypeIfPrimitive;
import static io.iamcyw.tower.utils.ReflectionUtils.unwrapIfType;


/**
 * Definition of handlers that can handle {@link QueryMessage}s. These handlers are wrapped with a {@link
 * QueryHandlingMember} that exposes query-specific handler information.
 */
public class MethodQueryMessageHandlerDefinition implements HandlerEnhancerDefinition {

    @Override
    public <T> MessageHandlingMember<T> wrapHandler(MessageHandlingMember<T> original) {
        return original.annotationAttributes(QueryHandler.class)
                       .map(attr -> (MessageHandlingMember<T>) new MethodQueryMessageHandlingMember<>(original, attr))
                       .orElse(original);
    }

    private static class MethodQueryMessageHandlingMember<T> extends WrappedMessageHandlingMember<T> implements QueryHandlingMember<T> {

        private final String queryName;

        private final Type resultType;

        public MethodQueryMessageHandlingMember(MessageHandlingMember<T> original, Map<String, Object> attr) {
            super(original);

            String queryNameAttribute = (String) attr.get("queryName");
            if ("".equals(queryNameAttribute)) {
                queryNameAttribute = original.payloadType().getName();
            }
            queryName = queryNameAttribute;

            resultType = original.unwrap(Method.class).map(this::queryResultType).orElseThrow(
                    () -> new UnsupportedHandlerException("@QueryHandler annotation can only be put on methods.",
                                                          original.unwrap(Member.class).orElse(null)));
            if (Void.TYPE.equals(resultType)) {
                throw new UnsupportedHandlerException(
                        "@QueryHandler annotated methods must not declare void return type",
                        original.unwrap(Member.class).orElse(null));
            }
        }

        @Override
        public Object handle(Message<?> message, T target) throws Exception {
            Object result = super.handle(message, target);
            if (result instanceof Optional) {
                return ((Optional<?>) result).orElse(null);
            }
            return result;
        }

        private Type queryResultType(Method method) {
            if (Void.class.equals(method.getReturnType())) {
                throw new UnsupportedHandlerException(
                        "@QueryHandler annotated methods must not declare void return type", method);
            }
            return unwrapType(method.getGenericReturnType());
        }

        private Type unwrapType(Type genericReturnType) {
            return upperBound(resolvePrimitiveWrapperTypeIfPrimitive(
                    unwrapIfType(genericReturnType, Future.class, Optional.class)));
        }

        private Type upperBound(Type type) {
            if (type instanceof WildcardType) {
                if (((WildcardType) type).getUpperBounds().length == 1) {
                    return ((WildcardType) type).getUpperBounds()[0];
                }
                return Object.class;
            }
            return type;
        }

        @Override
        public boolean canHandle(Message<?> message) {
            return super.canHandle(message) && message instanceof QueryMessage &&
                    queryName.equals(((QueryMessage<?, ?>) message).getQueryName()) &&
                    ((QueryMessage<?, ?>) message).getResponseType().matches(resultType);
        }

        @Override
        public String getQueryName() {
            return queryName;
        }

        public Type getResultType() {
            return resultType;
        }

    }

}
