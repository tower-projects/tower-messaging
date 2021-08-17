package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageMethod;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class DefaultReactorQueryBus implements ReactorQueryBus {

    private final Map<String, List<ReactorMessageMethod<QueryMessage>>> handles = new HashMap<>();

    private final List<ReactorQueryFilter> handlerInterceptors = new CopyOnWriteArrayList<>();

    @Override
    public <R> Uni<R> query(QueryMessage command) {

        Function<QueryMessage, Uni<R>> target = c -> lookupHandler(c).filter(
                messageHandler -> messageHandler.canHandle(command)).toUni().flatMap(
                messageHandler -> messageHandler.handle(c));

        return filter(command, target);
    }

    @Override
    public Registration subscribe(String queryName, ReactorMessageMethod<QueryMessage> handler) {
        List<ReactorMessageMethod<QueryMessage>> handlers = handles.getOrDefault(queryName, new ArrayList<>());
        handlers.add(handler);
        handles.put(queryName, handlers);
        return () -> handlers.remove(handler);
    }

    <C, R> Uni<R> filter(QueryMessage commandMessage, Function<QueryMessage, Uni<R>> target) {
        return DefaultReactorQueryFilterChain.buildChain(handlerInterceptors, target).filter(commandMessage);
    }

    <C> Multi<ReactorMessageMethod<QueryMessage>> lookupHandler(QueryMessage command) {
        return Multi.createFrom().iterable(handles.get(command.getQueryName()));
    }

    public Registration registerHandlerInterceptor(ReactorQueryFilter handlerInterceptor) {
        handlerInterceptors.add(handlerInterceptor);
        return () -> handlerInterceptors.remove(handlerInterceptor);
    }

}
