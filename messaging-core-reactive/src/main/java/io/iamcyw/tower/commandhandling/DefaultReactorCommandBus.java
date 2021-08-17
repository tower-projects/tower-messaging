package io.iamcyw.tower.commandhandling;

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

public class DefaultReactorCommandBus implements ReactorCommandBus {

    private final Map<String, List<ReactorMessageMethod<CommandMessage>>> handles = new HashMap<>();

    private final List<ReactorCommandFilter> handlerInterceptors = new CopyOnWriteArrayList<>();

    @Override
    public <R> Uni<R> dispatch(CommandMessage command) {

        Function<CommandMessage, Uni<R>> target = c -> lookupHandler(c).filter(
                messageHandler -> messageHandler.canHandle(command)).toUni().flatMap(
                messageHandler -> messageHandler.handle(c));

        return filter(command, target);
    }

    @Override
    public Registration subscribe(String commandName, ReactorMessageMethod<CommandMessage> handler) {
        List<ReactorMessageMethod<CommandMessage>> handlers = handles.getOrDefault(commandName, new ArrayList<>());
        handlers.add(handler);
        handles.put(commandName, handlers);
        return () -> handlers.remove(handler);
    }

    <C, R> Uni<R> filter(CommandMessage commandMessage, Function<CommandMessage, Uni<R>> target) {
        return DefaultReactorCommandFilterChain.buildChain(handlerInterceptors, target).filter(commandMessage);
    }

    <C> Multi<ReactorMessageMethod<CommandMessage>> lookupHandler(CommandMessage command) {
        return Multi.createFrom().iterable(handles.get(command.getCommandName()));
    }

    public Registration registerHandlerInterceptor(ReactorCommandFilter handlerInterceptor) {
        handlerInterceptors.add(handlerInterceptor);
        return () -> handlerInterceptors.remove(handlerInterceptor);
    }

}
