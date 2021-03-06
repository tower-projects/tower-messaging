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

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.common.transaction.NoTransactionManager;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.messaging.DefaultInterceptorChain;
import io.iamcyw.tower.messaging.MessageDispatchInterceptor;
import io.iamcyw.tower.messaging.MessageHandler;
import io.iamcyw.tower.messaging.MessageHandlerInterceptor;
import io.iamcyw.tower.messaging.interceptors.TransactionManagingInterceptor;
import io.iamcyw.tower.messaging.responsetypes.ResponseType;
import io.iamcyw.tower.messaging.unitofwork.DefaultUnitOfWork;
import io.iamcyw.tower.messaging.unitofwork.UnitOfWork;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.monitoring.NoOpMessageMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.iamcyw.tower.utils.ObjectUtils.getOrDefault;
import static io.iamcyw.tower.utils.ObjectUtils.getRemainingOfDeadline;
import static java.lang.String.format;

/**
 * Implementation of the QueryBus that dispatches queries to the handlers within the JVM. Any timeouts are ignored by
 * this implementation, as handlers are considered to answer immediately.
 * <p>
 * In case multiple handlers are registered for the same query and response type, the {@link #query(QueryMessage)}
 * method will invoke one of these handlers. Which one is unspecified.
 *
 * @author Marc Gathier
 * @author Allard Buijze
 * @author Steven van Beelen
 * @author Milan Savic
 * @since 3.1
 */
public class SimpleQueryBus implements QueryBus, QueryUpdateEmitter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleQueryBus.class);

    private final ConcurrentMap<String, CopyOnWriteArrayList<QuerySubscription>> subscriptions =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<SubscriptionQueryMessage<?, ?, ?>, ReentrantReadWriteLock> registeringSubscriptionQueryLocks = new ConcurrentHashMap<>();

    private final ConcurrentMap<SubscriptionQueryMessage<?, ?, ?>, UpdateHandler<?, ?>> updateHandlers =
            new ConcurrentHashMap<>();

    private final MessageMonitor<? super QueryMessage<?, ?>> messageMonitor;

    private final MessageMonitor<? super SubscriptionQueryUpdateMessage<?>> updateMessageMonitor;

    private final QueryInvocationErrorHandler errorHandler;

    private final List<MessageHandlerInterceptor<? super QueryMessage<?, ?>>> handlerInterceptors =
            new CopyOnWriteArrayList<>();

    private final List<MessageDispatchInterceptor<? super QueryMessage<?, ?>>> dispatchInterceptors =
            new CopyOnWriteArrayList<>();

    /**
     * Initialize the query bus without monitoring on messages and a {@link LoggingQueryInvocationErrorHandler}.
     */
    public SimpleQueryBus() {
        this(NoOpMessageMonitor.INSTANCE, NoTransactionManager.instance(),
             new LoggingQueryInvocationErrorHandler(logger));
    }

    /**
     * Initialize the query bus using given {@code transactionManager} to manage transactions around query execution
     * with. No monitoring is applied to messages and a {@link LoggingQueryInvocationErrorHandler} is used
     * to log errors on handlers during a scatter-gather query.
     *
     * @param transactionManager The transaction manager to manage transactions around query execution with
     */
    public SimpleQueryBus(TransactionManager transactionManager) {
        this(NoOpMessageMonitor.INSTANCE, transactionManager, new LoggingQueryInvocationErrorHandler(logger));
    }

    /**
     * Initialize the query bus with the given {@code messageMonitor} and given {@code errorHandler}.
     *
     * @param messageMonitor     The message monitor notified for incoming messages and their result
     * @param transactionManager The transaction manager to manage transactions around query execution with
     * @param errorHandler       The error handler to invoke when query handler report an error
     */
    public SimpleQueryBus(MessageMonitor<? super QueryMessage<?, ?>> messageMonitor,
                          TransactionManager transactionManager, QueryInvocationErrorHandler errorHandler) {
        this(messageMonitor, NoOpMessageMonitor.INSTANCE, transactionManager, errorHandler);
    }

    /**
     * Initialize the query bus with the given {@code messageMonitor} and given {@code errorHandler}.
     *
     * @param messageMonitor       The message monitor notified for incoming messages and their result
     * @param updateMessageMonitor The message monitor notified for incoming update message in regard to subscription
     *                             queries
     * @param transactionManager   The transaction manager to manage transactions around query execution with
     * @param errorHandler         The error handler to invoke when query handler report an error
     */
    public SimpleQueryBus(MessageMonitor<? super QueryMessage<?, ?>> messageMonitor,
                          MessageMonitor<? super SubscriptionQueryUpdateMessage<?>> updateMessageMonitor,
                          TransactionManager transactionManager, QueryInvocationErrorHandler errorHandler) {
        this.messageMonitor = messageMonitor != null ? messageMonitor : NoOpMessageMonitor.instance();
        this.updateMessageMonitor = updateMessageMonitor != null ? updateMessageMonitor : NoOpMessageMonitor.instance();
        this.errorHandler = getOrDefault(errorHandler, () -> new LoggingQueryInvocationErrorHandler(logger));
        if (transactionManager != null) {
            registerHandlerInterceptor(new TransactionManagingInterceptor<>(transactionManager));
        }
    }

    @Override
    public <R> Registration subscribe(String queryName, Type responseType,
                                      MessageHandler<? super QueryMessage<?, R>> handler) {
        CopyOnWriteArrayList<QuerySubscription> handlers = subscriptions
                .computeIfAbsent(queryName, k -> new CopyOnWriteArrayList<>());
        QuerySubscription<R> querySubscription = new QuerySubscription<>(responseType, handler);
        handlers.addIfAbsent(querySubscription);

        return () -> unsubscribe(queryName, querySubscription);
    }

    private boolean unsubscribe(String queryName, QuerySubscription querySubscription) {
        subscriptions.computeIfPresent(queryName, (key, handlers) -> {
            handlers.remove(querySubscription);
            if (handlers.isEmpty()) {
                return null;
            }
            return handlers;
        });
        return true;
    }

    @Override
    public <Q, R> CompletableFuture<QueryResponseMessage<R>> query(QueryMessage<Q, R> query) {
        MessageMonitor.MonitorCallback monitorCallback = messageMonitor.onMessageIngested(query);
        QueryMessage<Q, R> interceptedQuery = intercept(query);
        List<MessageHandler<? super QueryMessage<?, ?>>> handlers = getHandlersForMessage(interceptedQuery);
        CompletableFuture<QueryResponseMessage<R>> result = new CompletableFuture<>();
        try {
            if (handlers.isEmpty()) {
                throw new NoHandlerForQueryException(
                        format("No handler found for %s with response type %s", interceptedQuery.getQueryName(),
                               interceptedQuery.getResponseType()));
            }
            Iterator<MessageHandler<? super QueryMessage<?, ?>>> handlerIterator = handlers.iterator();
            boolean invocationSuccess = false;
            while (!invocationSuccess && handlerIterator.hasNext()) {
                try {
                    DefaultUnitOfWork<QueryMessage<Q, R>> uow = DefaultUnitOfWork.startAndGet(interceptedQuery);
                    result = interceptAndInvoke(uow, handlerIterator.next());
                    invocationSuccess = true;
                } catch (NoHandlerForQueryException e) {
                    // Ignore this Query Handler, as we may have another one which is suitable
                }
            }
            if (!invocationSuccess) {
                throw new NoHandlerForQueryException(
                        format("No suitable handler was found for %s with response type %s",
                               interceptedQuery.getQueryName(), interceptedQuery.getResponseType()));
            }
            monitorCallback.reportSuccess();
        } catch (Exception e) {
            result.completeExceptionally(e);
            monitorCallback.reportFailure(e);
        }
        return result;
    }

    @Override
    public <Q, R> Stream<QueryResponseMessage<R>> scatterGather(QueryMessage<Q, R> query, long timeout, TimeUnit unit) {
        MessageMonitor.MonitorCallback monitorCallback = messageMonitor.onMessageIngested(query);
        QueryMessage<Q, R> interceptedQuery = intercept(query);
        List<MessageHandler<? super QueryMessage<?, ?>>> handlers = getHandlersForMessage(interceptedQuery);
        if (handlers.isEmpty()) {
            monitorCallback.reportIgnored();
            return Stream.empty();
        }

        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        return handlers.stream().map(handler -> {
            try {
                long leftTimeout = getRemainingOfDeadline(deadline);
                QueryResponseMessage<R> response = interceptAndInvoke(DefaultUnitOfWork.startAndGet(interceptedQuery),
                                                                      handler).get(leftTimeout, TimeUnit.MILLISECONDS);
                monitorCallback.reportSuccess();
                return response;
            } catch (Exception e) {
                monitorCallback.reportFailure(e);
                errorHandler.onError(e, interceptedQuery, handler);
                return null;
            }
        }).filter(Objects::nonNull);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Q, I, U> Registration subscriptionQuery(SubscriptionQueryMessage<Q, I, U> query,
                                                    UpdateHandler<I, U> updateHandler) {
        MessageMonitor.MonitorCallback monitorCallback = messageMonitor.onMessageIngested(query);
        SubscriptionQueryMessage<Q, I, U> interceptedQuery = intercept(query);
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
        registeringSubscriptionQueryLocks.put(interceptedQuery, rwLock);
        List<MessageHandler<? super QueryMessage<?, ?>>> handlers = getHandlersForMessage(interceptedQuery);
        rwLock.writeLock().lock();
        try {
            if (handlers.isEmpty()) {
                throw new NoHandlerForQueryException(
                        format("No handler found for %s with response type %s and update type %s",
                               interceptedQuery.getQueryName(), interceptedQuery.getResponseType(),
                               interceptedQuery.getUpdateResponseType()));
            }
            Iterator<MessageHandler<? super QueryMessage<?, ?>>> handlerIterator = handlers.iterator();
            boolean invocationSuccess = false;
            while (!invocationSuccess && handlerIterator.hasNext()) {
                try {
                    DefaultUnitOfWork<QueryMessage<Q, I>> uow = DefaultUnitOfWork.startAndGet(interceptedQuery);
                    interceptAndInvoke(uow, handlerIterator.next()).thenAccept(responseMessage -> {
                        try {
                            I initialResponse = responseMessage.getPayload();
                            updateHandler.onInitialResult(initialResponse);
                            updateHandlers.put(query, updateHandler);
                            registeringSubscriptionQueryLocks.remove(interceptedQuery);
                            monitorCallback.reportSuccess();
                        } catch (Exception e) {
                            monitorCallback.reportFailure(e);
                            updateHandler.onCompletedExceptionally(e);
                        }
                    });

                    invocationSuccess = true;
                } catch (NoHandlerForQueryException e) {
                    // Ignore this Query Handler, as we may have another one which is suitable
                }
            }
            if (!invocationSuccess) {
                throw new NoHandlerForQueryException(
                        format("No suitable handler was found for %s with response type %s and update type %s",
                               interceptedQuery.getQueryName(), interceptedQuery.getResponseType(),
                               interceptedQuery.getUpdateResponseType()));
            }
        } catch (Exception e) {
            monitorCallback.reportFailure(e);
            updateHandler.onCompletedExceptionally(e);
        } finally {
            rwLock.writeLock().unlock();
        }

        return () -> {
            updateHandlers.remove(query);
            return true;
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> void emit(Predicate<SubscriptionQueryMessage<?, ?, U>> filter,
                         SubscriptionQueryUpdateMessage<U> update) {
        registeringSubscriptionQueryReadSafe(filter, () -> {
            MessageMonitor.MonitorCallback monitorCallback = updateMessageMonitor.onMessageIngested(update);
            List<? extends SubscriptionQueryMessage<?, ?, U>> queries = updateHandlers.keySet().stream()
                                                                                      .filter(sqm -> filter
                                                                                              .test((SubscriptionQueryMessage<?, ?, U>) sqm))
                                                                                      .map(m -> (SubscriptionQueryMessage<?, ?, U>) m)
                                                                                      .collect(Collectors.toList());
            if (queries.isEmpty()) {
                monitorCallback.reportIgnored();
            } else {
                queries.forEach(query -> {
                    UpdateHandler<?, U> updateHandler = (UpdateHandler<?, U>) updateHandlers.get(query);
                    try {
                        updateHandler.onUpdate(update.getPayload());
                        monitorCallback.reportSuccess();
                    } catch (Exception e) {
                        logger.error(format("An error happened while trying to emit an update to a query: %s.", query),
                                     e);
                        monitorCallback.reportFailure(e);
                        updateHandlers.remove(query);
                        updateHandler.onCompletedExceptionally(e);
                    }
                });
            }
        });
    }

    @Override
    public void complete(Predicate<SubscriptionQueryMessage<?, ?, ?>> filter) {
        registeringSubscriptionQueryReadSafe(filter, () -> updateHandlers.keySet().stream().filter(filter).forEach(
                query -> updateHandlers.remove(query).onCompleted()));
    }

    @Override
    public void completeExceptionally(Predicate<SubscriptionQueryMessage<?, ?, ?>> filter, Throwable cause) {
        registeringSubscriptionQueryReadSafe(filter, () -> updateHandlers.keySet().stream().filter(filter).forEach(
                query -> updateHandlers.remove(query).onCompletedExceptionally(cause)));
    }

    /**
     * Makes sure that {@code subscriptionQueryUpdate} is executed with the read lock on all subscription queries which
     * are currently in registration process.
     *
     * @param subscriptionQueryFilter used to filter out subscription queries which are currently in registration
     *                                process
     * @param subscriptionQueryUpdate operation to be executed in lock safe guard
     */
    @SuppressWarnings("unchecked")
    private void registeringSubscriptionQueryReadSafe(Predicate<?> subscriptionQueryFilter,
                                                      Runnable subscriptionQueryUpdate) {
        List<ReentrantReadWriteLock> locks = registeringSubscriptionQueryLocks.keySet().stream()
                                                                              .filter((Predicate<?
                                                                                      super SubscriptionQueryMessage<
                                                                                      ?, ?, ?>>) subscriptionQueryFilter)
                                                                              .map(registeringSubscriptionQueryLocks::get)
                                                                              .collect(Collectors.toList());

        locks.forEach(lock -> lock.readLock().lock());
        try {
            subscriptionQueryUpdate.run();
        } finally {
            locks.forEach(lock -> lock.readLock().unlock());
        }
    }

    @SuppressWarnings("unchecked")
    private <Q, R> CompletableFuture<QueryResponseMessage<R>> interceptAndInvoke(UnitOfWork<QueryMessage<Q, R>> uow,
                                                                                 MessageHandler<? super QueryMessage<
                                                                                         ?, R>> handler) throws Exception {
        return uow.executeWithResult(() -> {
            ResponseType<R> responseType = uow.getMessage().getResponseType();
            Object queryResponse = new DefaultInterceptorChain<>(uow, handlerInterceptors, handler).proceed();
            if (queryResponse instanceof CompletableFuture) {
                return ((CompletableFuture) queryResponse)
                        .thenCompose(result -> buildCompletableFuture(responseType, result));
            } else if (queryResponse instanceof Future) {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        return ((Future) queryResponse).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new QueryExecutionException("Error happened while trying to execute query handler", e);
                    }
                });
            }
            return buildCompletableFuture(responseType, queryResponse);
        });
    }

    private <R> CompletableFuture<QueryResponseMessage<R>> buildCompletableFuture(ResponseType<R> responseType,
                                                                                  Object queryResponse) {
        return CompletableFuture.completedFuture(GenericQueryResponseMessage.asNullableResponseMessage(
                responseType.responseMessagePayloadType(), responseType.convert(queryResponse)));
    }

    @SuppressWarnings("unchecked")
    private <Q, R, T extends QueryMessage<Q, R>> T intercept(T query) {
        T intercepted = query;
        for (MessageDispatchInterceptor<? super QueryMessage<?, ?>> interceptor : dispatchInterceptors) {
            intercepted = (T) interceptor.handle(intercepted);
        }
        return intercepted;
    }

    /**
     * Returns the subscriptions for this query bus. While the returned map is unmodifiable, it may or may not reflect
     * changes made to the subscriptions after the call was made.
     *
     * @return the subscriptions for this query bus
     */
    protected Map<String, Collection<QuerySubscription>> getSubscriptions() {
        return Collections.unmodifiableMap(subscriptions);
    }

    /**
     * Registers an interceptor that is used to intercept Queries before they are passed to their
     * respective handlers. The interceptor is invoked separately for each handler instance (in a separate unit of
     * work).
     *
     * @param interceptor the interceptor to invoke before passing a Query to the handler
     * @return handle to unregister the interceptor
     */
    public Registration registerHandlerInterceptor(MessageHandlerInterceptor<? super QueryMessage<?, ?>> interceptor) {
        handlerInterceptors.add(interceptor);
        return () -> handlerInterceptors.remove(interceptor);
    }

    /**
     * Registers an interceptor that intercepts Queries as they are sent. Each interceptor is called
     * once, regardless of the type of query (point-to-point or scatter-gather) executed.
     *
     * @param interceptor the interceptor to invoke when sending a Query
     * @return handle to unregister the interceptor
     */
    public Registration registerDispatchInterceptor(
            MessageDispatchInterceptor<? super QueryMessage<?, ?>> interceptor) {
        dispatchInterceptors.add(interceptor);
        return () -> dispatchInterceptors.remove(interceptor);
    }

    @SuppressWarnings("unchecked") // Suppresses 'queryHandler' cast to `MessageHandler<? super QueryMessage<?, ?>>`
    private <Q, R> List<MessageHandler<? super QueryMessage<?, ?>>> getHandlersForMessage(
            QueryMessage<Q, R> queryMessage) {
        ResponseType<R> responseType = queryMessage.getResponseType();
        return subscriptions.computeIfAbsent(queryMessage.getQueryName(), k -> new CopyOnWriteArrayList<>()).stream()
                            .filter(querySubscription -> responseType.matches(querySubscription.getResponseType()))
                            .map((Function<QuerySubscription, MessageHandler>) QuerySubscription::getQueryHandler)
                            .map(queryHandler -> (MessageHandler<? super QueryMessage<?, ?>>) queryHandler)
                            .collect(Collectors.toList());
    }

}
