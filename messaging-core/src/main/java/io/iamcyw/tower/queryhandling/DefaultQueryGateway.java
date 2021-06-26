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
package io.iamcyw.tower.queryhandling;


import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.MessageDispatchInterceptor;
import io.iamcyw.tower.messaging.responsetypes.ResponseType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Implementation of the QueryGateway interface that allows the registration of dispatchInterceptors.
 */
public class DefaultQueryGateway implements QueryGateway {

    private final QueryBus queryBus;

    private final MessageDispatchInterceptor<? super QueryMessage<?, ?>>[] dispatchInterceptors;

    /**
     * Initializes the gateway to send queries to the given {@code queryBus} and invoking given
     * {@code dispatchInterceptors} prior to publication ont he query bus.
     *
     * @param queryBus             The bus to deliver messages on
     * @param dispatchInterceptors The interceptors to invoke prior to publication on the bus
     */
    @SafeVarargs
    public DefaultQueryGateway(QueryBus queryBus,
                               MessageDispatchInterceptor<? super QueryMessage<?, ?>>... dispatchInterceptors) {
        this.queryBus = queryBus;
        this.dispatchInterceptors = dispatchInterceptors;
    }

    @Override
    public <R, Q> CompletableFuture<R> query(String queryName, Q query, ResponseType<R> responseType) {
        return queryBus.query(processInterceptors(new GenericQueryMessage<>(query, queryName, responseType)))
                       .thenApply(QueryResponseMessage::getPayload);
    }

    @Override
    public <R, Q> Stream<R> scatterGather(String queryName, Q query, ResponseType<R> responseType, long timeout,
                                          TimeUnit timeUnit) {
        GenericQueryMessage<Q, R> queryMessage = new GenericQueryMessage<>(query, queryName, responseType);
        return queryBus.scatterGather(processInterceptors(queryMessage), timeout, timeUnit)
                       .map(QueryResponseMessage::getPayload);
    }

    @Override
    public <Q, I, U> Registration subscriptionQuery(String queryName, Q query, ResponseType<I> initialResponseType,
                                                    ResponseType<U> updateResponseType,
                                                    UpdateHandler<I, U> updateHandler) {
        SubscriptionQueryMessage<Q, I, U> subscriptionQueryMessage = new GenericSubscriptionQueryMessage<>(query,
                                                                                                           queryName,
                                                                                                           initialResponseType,
                                                                                                           updateResponseType);
        return queryBus.subscriptionQuery(processInterceptors(subscriptionQueryMessage), updateHandler);
    }

    @SuppressWarnings("unchecked")
    private <Q, R, T extends QueryMessage<Q, R>> T processInterceptors(T query) {
        T intercepted = query;
        for (MessageDispatchInterceptor<? super QueryMessage<?, ?>> interceptor : dispatchInterceptors) {
            intercepted = (T) interceptor.handle(intercepted);
        }
        return intercepted;
    }

}
