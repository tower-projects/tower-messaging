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
package io.iamcyw.tower.queryhandling.annotation;


import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.MessageHandler;
import io.iamcyw.tower.messaging.annotation.AnnotatedHandlerInspector;
import io.iamcyw.tower.messaging.annotation.ClasspathParameterResolverFactory;
import io.iamcyw.tower.messaging.annotation.MessageHandlingMember;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.queryhandling.*;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that turns any {@link QueryHandler @QueryHandler} annotated bean into a {@link MessageHandler}
 * implementation. Each annotated method is subscribed as a QueryHandler at the {@link QueryBus} for the query type
 * specified by the parameter/return type of that method.
 */
public class AnnotationQueryHandlerAdapter<T> implements QueryHandlerAdapter, MessageHandler<QueryMessage<?, ?>> {

    private final T target;

    private final AnnotatedHandlerInspector<T> model;

    /**
     * Initializes the adapter, forwarding call to the given {@code target}.
     *
     * @param target The instance with {@link QueryHandler} annotated methods
     */
    public AnnotationQueryHandlerAdapter(T target) {
        this(target, ClasspathParameterResolverFactory.forClass(target.getClass()));
    }

    /**
     * Initializes the adapter, forwarding call to the given {@code target}, resolving parameters using the given
     * {@code parameterResolverFactory}.
     *
     * @param target                   The instance with {@link QueryHandler} annotated methods
     * @param parameterResolverFactory The parameter resolver factory to resolve handler parameters with
     */
    @SuppressWarnings("unchecked")
    public AnnotationQueryHandlerAdapter(T target, ParameterResolverFactory parameterResolverFactory) {
        this.model = AnnotatedHandlerInspector.inspectType((Class<T>) target.getClass(), parameterResolverFactory);
        this.target = target;
    }

    public Registration subscribe(QueryBus queryBus) {
        Collection<Registration> registrationList = model.getHandlers().stream().map(m -> subscribe(queryBus, m))
                                                         .filter(Objects::nonNull).collect(Collectors.toList());
        return () -> registrationList.stream().map(Registration::cancel).reduce(Boolean::logicalOr).orElse(false);
    }

    @Override
    public Object handle(QueryMessage<?, ?> message) throws Exception {
        for (MessageHandlingMember<? super T> member : model.getHandlers()) {
            if (member.canHandle(message)) {
                return member.handle(message, target);
            }
        }
        throw new NoHandlerForQueryException(
                "No suitable handler was found for the query of type " + message.getPayloadType().getName());
    }

    @SuppressWarnings("unchecked")
    private Registration subscribe(QueryBus queryBus, MessageHandlingMember<? super T> m) {
        Optional<QueryHandlingMember> unwrappedQueryMember = m.unwrap(QueryHandlingMember.class);
        if (unwrappedQueryMember.isPresent()) {
            QueryHandlingMember qhm = unwrappedQueryMember.get();
            return queryBus.subscribe(qhm.getQueryName(), qhm.getResultType(), this);
        }

        return null;
    }

}
