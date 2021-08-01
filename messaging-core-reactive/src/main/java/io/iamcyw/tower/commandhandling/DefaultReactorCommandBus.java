package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.smallrye.mutiny.Multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class DefaultReactorCommandBus implements ReactorCommandBus {

    private final Map<String, List<ReactorMessageHandler<CommandMessage<?>>>> handles = new HashMap<>();

    private final List<ReactorCommandFilter> handlerInterceptors = new CopyOnWriteArrayList<>();

    @Override
    public <C, R> Multi<R> dispatch(CommandMessage<C> command) {

        Function<CommandMessage<C>, Multi<R>> target = c -> lookupHandler(c).filter(
                messageHandler -> messageHandler.canHandle(command)).toUni().onItem().transformToMulti(
                messageHandler -> messageHandler.handle(c));

        return filter(command, target);
    }

    @Override
    public Registration subscribe(String commandName, ReactorMessageHandler<CommandMessage<?>> handler) {
        List<ReactorMessageHandler<CommandMessage<?>>> handlers = handles.getOrDefault(commandName, new ArrayList<>());
        handlers.add(handler);
        handles.put(commandName, handlers);
        return () -> handlers.remove(handler);
    }

    <C, R> Multi<R> filter(CommandMessage<C> commandMessage, Function<CommandMessage<C>, Multi<R>> target) {
        return new DefaultReactorCommandFilterChain(handlerInterceptors).filter(commandMessage, target);
    }

    <C> Multi<ReactorMessageHandler<CommandMessage<?>>> lookupHandler(CommandMessage<C> command) {
        return Multi.createFrom().iterable(handles.get(command.getCommandName()));
    }

    public Registration registerHandlerInterceptor(ReactorCommandFilter handlerInterceptor) {
        handlerInterceptors.add(handlerInterceptor);
        return () -> handlerInterceptors.remove(handlerInterceptor);
    }

}
