package io.iamcyw.tower.commandhandling.gateway;

import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.commandhandling.GenericCommandMessage;
import io.iamcyw.tower.commandhandling.ReactorCommandBus;
import io.iamcyw.tower.messaging.MessageDispatchInterceptor;
import io.smallrye.mutiny.Uni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultReactorCommandGateway implements ReactorCommandGateway {

    private final ReactorCommandBus reactorCommandBus;

    private final List<MessageDispatchInterceptor<? super CommandMessage>> dispatchInterceptors;

    public DefaultReactorCommandGateway(ReactorCommandBus reactorCommandBus,
                                        MessageDispatchInterceptor<? super CommandMessage>... dispatchInterceptors) {
        this(reactorCommandBus, Arrays.asList(dispatchInterceptors));
    }

    public DefaultReactorCommandGateway(ReactorCommandBus reactorCommandBus,
                                        List<MessageDispatchInterceptor<? super CommandMessage>> dispatchInterceptors) {
        this.reactorCommandBus = reactorCommandBus;
        if (dispatchInterceptors != null && !dispatchInterceptors.isEmpty()) {
            this.dispatchInterceptors = new ArrayList<>(dispatchInterceptors);
        } else {
            this.dispatchInterceptors = Collections.emptyList();
        }
    }

    @Override
    public <R> Uni<R> send(Object command) {
        return createCommandMessage(command).flatMap(reactorCommandBus::dispatch);
    }

    Uni<CommandMessage> createCommandMessage(Object command) {
        return Uni.createFrom().item(command).onItem().transform(GenericCommandMessage::asCommandMessage)
                  .map(this::processInterceptors);
    }

    protected CommandMessage processInterceptors(CommandMessage commandMessage) {
        CommandMessage message = commandMessage;
        for (MessageDispatchInterceptor<? super CommandMessage> dispatchInterceptor : dispatchInterceptors) {
            message = (CommandMessage) dispatchInterceptor.handle(message);
        }
        return message;
    }

}
