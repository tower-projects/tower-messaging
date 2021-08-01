package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.iamcyw.tower.messaging.responsetypes.ResponseType;
import io.smallrye.mutiny.Multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DefaultReactorQueryBus implements ReactorQueryBus {

    private final Map<String, List<ReactorMessageHandler<QueryMessage<?, ?>>>> handles = new HashMap<>();

    private final List<ReactorQueryFilter> handlerInterceptors = new CopyOnWriteArrayList<>();


    @Override
    public <Q, R> Multi<QueryResponseMessage<R>> query(QueryMessage<Q, R> query) {
        Function<QueryMessage<Q, R>, Multi<QueryResponseMessage<R>>> target = q -> lookupHandler(q).filter(
                handle -> handle.canHandle(q)).flatMap(handle -> handle.handle(q)).map(result -> buildCompletableFuture(
                q.getResponseType(), result));
        return filter(query, target);
    }

    @Override
    public <Q, R> Multi<QueryResponseMessage<R>> scatterGather(QueryMessage<Q, R> queryMessage, long timeout,
                                                               TimeUnit unit) {
        return null;
    }

    @Override
    public Registration subscribe(String queryName, ReactorMessageHandler<QueryMessage<?, ?>> handler) {
        List<ReactorMessageHandler<QueryMessage<?, ?>>> handlers = handles.getOrDefault(queryName, new ArrayList<>());
        handlers.add(handler);
        handles.put(queryName, handlers);
        return () -> handlers.remove(handler);
    }

    private <R> QueryResponseMessage<R> buildCompletableFuture(ResponseType<R> responseType, Object queryResponse) {
        return GenericQueryResponseMessage.asNullableResponseMessage(responseType.responseMessagePayloadType(),
                                                                     responseType.convert(queryResponse));
    }

    <Q, R> Multi<QueryResponseMessage<R>> filter(QueryMessage<Q, R> queryMessage,
                                                 Function<QueryMessage<Q, R>, Multi<QueryResponseMessage<R>>> target) {
        return new DefaultReactorQueryFilterChain(handlerInterceptors).filter(queryMessage, target);
    }

    <Q, R> Multi<ReactorMessageHandler<QueryMessage<?, ?>>> lookupHandler(QueryMessage<Q, R> query) {
        return Multi.createFrom().iterable(handles.get(query.getQueryName()));
    }

}
