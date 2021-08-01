package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.commandhandling.handler.CommandMessageGroupByNameHandles;
import io.iamcyw.tower.commandhandling.handler.CommandMessageHandler;
import io.iamcyw.tower.common.Registration;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class DefaultReactorCommandBus implements ReactorCommandBus {

    private final CommandMessageGroupByNameHandles handles;

    private final List<ReactorCommandFilter> handlerInterceptors = new CopyOnWriteArrayList<>();

    public DefaultReactorCommandBus(CommandMessageGroupByNameHandles handles) {
        this.handles = handles;
    }

    @Override
    public <C, R> Uni<R> dispatch(CommandMessage<C> command) {

        Function<CommandMessage<C>, Uni<R>> target = c -> lookupHandler(c).map(
                messageHandler -> (R) messageHandler.handle(c));

        return filter(command, target);
    }

    <C, R> Uni<R> filter(CommandMessage<C> commandMessage, Function<CommandMessage<C>, Uni<R>> target) {
        return new DefaultReactorCommandFilterChain(handlerInterceptors).filter(commandMessage, target);
    }

    <C> Uni<CommandMessageHandler<C>> lookupHandler(CommandMessage<C> command) {
        return handles.<C>lookupHandler(command.getCommandName())
                      .filter(messageHandler -> messageHandler.canHandle(command)).toUni();
    }

    public Registration registerHandlerInterceptor(ReactorCommandFilter handlerInterceptor) {
        handlerInterceptors.add(handlerInterceptor);
        return () -> handlerInterceptors.remove(handlerInterceptor);
    }

}
