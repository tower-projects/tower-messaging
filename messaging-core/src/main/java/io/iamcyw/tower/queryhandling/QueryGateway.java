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
import io.iamcyw.tower.messaging.responsetypes.ResponseType;
import io.iamcyw.tower.messaging.responsetypes.ResponseTypes;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Interface towards the Query Handling components of an application. This interface provides a friendlier API toward
 * the query bus.
 */
public interface QueryGateway {

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response with
     * the given {@code responseType} from a single source. The query name will be derived from the provided
     * {@code query}. Execution may be asynchronous, depending on the QueryBus implementation.
     *
     * @param query        The {@code query} to be sent
     * @param responseType A {@link java.lang.Class} describing the desired response type
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link java.util.concurrent.CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    default <R, Q> CompletableFuture<R> query(Q query, Class<R> responseType) {
        return query(query.getClass().getName(), query, responseType);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response with
     * the given {@code responseType} from a single source. Execution may be asynchronous, depending on the QueryBus
     * implementation.
     *
     * @param queryName    A {@link java.lang.String} describing the query to be executed
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link java.util.concurrent.CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    default <R, Q> CompletableFuture<R> query(String queryName, Q query, Class<R> responseType) {
        return query(queryName, query, ResponseTypes.instanceOf(responseType));
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from a single source. The query name will be derived from the provided
     * {@code query}. Execution may be asynchronous, depending on the QueryBus implementation.
     *
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link java.util.concurrent.CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    default <R, Q> CompletableFuture<R> query(Q query, ResponseType<R> responseType) {
        return query(query.getClass().getName(), query, responseType);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from a single source. Execution may be asynchronous, depending on the QueryBus
     * implementation.
     *
     * @param queryName    A {@link java.lang.String} describing the query to be executed
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link java.util.concurrent.CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    <R, Q> CompletableFuture<R> query(String queryName, Q query, ResponseType<R> responseType);

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from several sources. The stream is completed when a {@code timeout} occurs or when
     * all results are received. The query name will be derived from the provided {@code query}. Execution may be
     * asynchronous, depending on the QueryBus implementation.
     *
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param timeout      A timeout of {@code long} for the query
     * @param timeUnit     The selected {@link java.util.concurrent.TimeUnit} for the given {@code timeout}
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A stream of results.
     */
    default <R, Q> Stream<R> scatterGather(Q query, ResponseType<R> responseType, long timeout, TimeUnit timeUnit) {
        return scatterGather(query.getClass().getName(), query, responseType, timeout, timeUnit);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from several sources. The stream is completed when a {@code timeout} occurs or when
     * all results are received. Execution may be asynchronous, depending on the QueryBus implementation.
     *
     * @param queryName    A {@link java.lang.String} describing the query to be executed
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param timeout      A timeout of {@code long} for the query
     * @param timeUnit     The selected {@link java.util.concurrent.TimeUnit} for the given {@code timeout}
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A stream of results.
     */
    <R, Q> Stream<R> scatterGather(String queryName, Q query, ResponseType<R> responseType, long timeout,
                                   TimeUnit timeUnit);

    /**
     * Sends given {@code query} over the {@link QueryBus} and uses {@code updateHandler} to inform caller about initial
     * response and incremental updates. Returned registration can be used to cancel receiving updates.
     *
     * @param query               The {@code query} to be sent
     * @param initialResponseType The initial response type used for this query
     * @param updateResponseType  The update response type used for this query
     * @param updateHandler       The handler to be invoked when there is an initial response to be sent and when there
     *                            are incremental updates
     * @param <Q>                 The type of the query
     * @param <I>                 The type of the initial response
     * @param <U>                 The type of the incremental update
     * @return registration which can be used to cancel receiving updates
     */
    default <Q, I, U> Registration subscriptionQuery(Q query, Class<I> initialResponseType, Class<U> updateResponseType,
                                                     UpdateHandler<I, U> updateHandler) {
        return subscriptionQuery(query.getClass().getName(), query, initialResponseType, updateResponseType,
                                 updateHandler);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus} and uses {@code updateHandler} to inform caller about initial
     * response and incremental updates. Returned registration can be used to cancel receiving updates.
     *
     * @param queryName           A {@link String} describing query to be executed
     * @param query               The {@code query} to be sent
     * @param initialResponseType The initial response type used for this query
     * @param updateResponseType  The update response type used for this query
     * @param updateHandler       The handler to be invoked when there is an initial response to be sent and when there
     *                            are incremental updates
     * @param <Q>                 The type of the query
     * @param <I>                 The type of the initial response
     * @param <U>                 The type of the incremental update
     * @return registration which can be used to cancel receiving updates
     */
    default <Q, I, U> Registration subscriptionQuery(String queryName, Q query, Class<I> initialResponseType,
                                                     Class<U> updateResponseType, UpdateHandler<I, U> updateHandler) {
        return subscriptionQuery(queryName, query, ResponseTypes.instanceOf(initialResponseType),
                                 ResponseTypes.instanceOf(updateResponseType), updateHandler);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus} and uses {@code updateHandler} to inform caller about initial
     * response and incremental updates. Returned registration can be used to cancel receiving updates.
     *
     * @param query               The {@code query} to be sent
     * @param initialResponseType The initial response type used for this query
     * @param updateResponseType  The update response type used for this query
     * @param updateHandler       The handler to be invoked when there is an initial response to be sent and when there
     *                            are incremental updates
     * @param <Q>                 The type of the query
     * @param <I>                 The type of the initial response
     * @param <U>                 The type of the incremental update
     * @return registration which can be used to cancel receiving updates
     */
    default <Q, I, U> Registration subscriptionQuery(Q query, ResponseType<I> initialResponseType,
                                                     ResponseType<U> updateResponseType,
                                                     UpdateHandler<I, U> updateHandler) {
        return subscriptionQuery(query.getClass().getName(), query, initialResponseType, updateResponseType,
                                 updateHandler);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus} and uses {@code updateHandler} to inform caller about initial
     * response and incremental updates. Returned registration can be used to cancel receiving updates.
     *
     * @param queryName           A {@link String} describing query to be executed
     * @param query               The {@code query} to be sent
     * @param initialResponseType The initial response type used for this query
     * @param updateResponseType  The update response type used for this query
     * @param updateHandler       The handler to be invoked when there is an initial response to be sent and when there
     *                            are incremental updates
     * @param <Q>                 The type of the query
     * @param <I>                 The type of the initial response
     * @param <U>                 The type of the incremental update
     * @return registration which can be used to cancel receiving updates
     */
    <Q, I, U> Registration subscriptionQuery(String queryName, Q query, ResponseType<I> initialResponseType,
                                             ResponseType<U> updateResponseType, UpdateHandler<I, U> updateHandler);

    /**
     * Sends given query to the query bus and expects a result of type resultClass. Execution may be asynchronous.
     *
     * @param query        The query.
     * @param queryName    The name of the query.
     * @param responseType The expected result type.
     * @param <R>          The type of result expected from query execution.
     * @param <Q>          The query class.
     * @return A completable future that contains the first result of the query..
     * @throws NullPointerException when query is null.
     * @deprecated Use {@link #query(String, Object, Class)} instead.
     */
    @Deprecated
    default <R, Q> CompletableFuture<R> send(String queryName, Q query, Class<R> responseType) {
        return query(queryName, query, responseType);
    }

    /**
     * Sends given query to the query bus and expects a result of type resultClass. Execution may be asynchronous.
     *
     * @param query        The query.
     * @param responseType The expected result type.
     * @param <R>          The type of result expected from query execution.
     * @param <Q>          The query class.
     * @return A completable future that contains the first result of the query.
     * @throws NullPointerException when query is null.
     * @deprecated Use {@link #query(Object, Class)} instead.
     */
    @Deprecated
    default <R, Q> CompletableFuture<R> send(Q query, Class<R> responseType) {
        return query(query.getClass().getName(), query, responseType);
    }

    /**
     * Sends given query to the query bus and expects a stream of results with type responseType. The stream is
     * completed when a timeout occurs or when all results are received.
     *
     * @param query        The query.
     * @param responseType The expected result type.
     * @param timeout      Timeout for the request.
     * @param timeUnit     Unit for the timeout.
     * @param <R>          The type of result expected from query execution.
     * @param <Q>          The query class.
     * @return A stream of results.
     * @throws NullPointerException when query is null.
     * @deprecated Use {@link #scatterGather(Object, ResponseType, long, TimeUnit)} instead.
     */
    @Deprecated
    default <R, Q> Stream<R> send(Q query, Class<R> responseType, long timeout, TimeUnit timeUnit) {
        return scatterGather(query, ResponseTypes.instanceOf(responseType), timeout, timeUnit);
    }

    /**
     * Sends given query to the query bus and expects a stream of results with name resultName. The stream is completed
     * when a timeout occurs or when all results are received.
     *
     * @param query       The query.
     * @param queryName   The name of the query.
     * @param resultClass Type type of result.
     * @param timeout     Timeout for the request.
     * @param timeUnit    Unit for the timeout.
     * @param <R>         The type of result expected from query execution.
     * @param <Q>         The query class.
     * @return A stream of results.
     * @deprecated Use {@link #scatterGather(String, Object, ResponseType, long, TimeUnit)} instead.
     */
    @Deprecated
    default <R, Q> Stream<R> send(Q query, String queryName, Class<R> resultClass, long timeout, TimeUnit timeUnit) {
        return scatterGather(queryName, query, ResponseTypes.instanceOf(resultClass), timeout, timeUnit);
    }

}
