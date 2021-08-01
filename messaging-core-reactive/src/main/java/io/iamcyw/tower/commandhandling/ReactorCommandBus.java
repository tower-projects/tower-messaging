package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.smallrye.mutiny.Multi;

public interface ReactorCommandBus {

    <C, R> Multi<R> dispatch(CommandMessage<C> command);

    Registration subscribe(String commandName, ReactorMessageHandler<CommandMessage<?>> handler);

}
