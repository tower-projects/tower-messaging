package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.smallrye.mutiny.Multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class DefaultReactorQueryBus implements ReactorQueryBus {

    private final Map<String, List<ReactorMessageHandler<QueryMessage>>> handles = new HashMap<>();

    private final List<ReactorQueryFilter> handlerInterceptors = new CopyOnWriteArrayList<>();


    @Override
    public <R> Multi<R> query(QueryMessage query) {
        Function<QueryMessage, Multi<R>> target = q -> lookupHandler(q).filter(handle -> handle.canHandle(q))
                                                                       .flatMap(handle -> handle.handle(q));
        return filter(query, target);
    }

    @Override
    public Registration subscribe(String queryName, ReactorMessageHandler<QueryMessage> handler) {
        List<ReactorMessageHandler<QueryMessage>> handlers = handles.getOrDefault(queryName, new ArrayList<>());
        handlers.add(handler);
        handles.put(queryName, handlers);
        return () -> handlers.remove(handler);
    }


    <Q, R> Multi<R> filter(QueryMessage queryMessage, Function<QueryMessage, Multi<R>> target) {
        return new DefaultReactorQueryFilterChain(handlerInterceptors).filter(queryMessage);
    }

    <Q, R> Multi<ReactorMessageHandler<QueryMessage>> lookupHandler(QueryMessage query) {
        return Multi.createFrom().iterable(handles.get(query.getQueryName()));
    }

}
