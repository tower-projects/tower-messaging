package io.iamcyw.tower.commandhandling.gateway;

import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.commandhandling.GenericCommandMessage;
import io.iamcyw.tower.commandhandling.ReactorCommandBus;
import io.smallrye.mutiny.Uni;

public class DefaultReactorCommandGateway implements ReactorCommandGateway {

    private final ReactorCommandBus reactorCommandBus;

    public DefaultReactorCommandGateway(ReactorCommandBus reactorCommandBus) {
        this.reactorCommandBus = reactorCommandBus;
    }

    @Override
    public <R> Uni<R> send(Object command) {
        return createCommandMessage(command).flatMap(reactorCommandBus::dispatch);
    }

    Uni<CommandMessage<?>> createCommandMessage(Object command) {
        return Uni.createFrom().item(command).onItem().transform(GenericCommandMessage::asCommandMessage);
    }

}
