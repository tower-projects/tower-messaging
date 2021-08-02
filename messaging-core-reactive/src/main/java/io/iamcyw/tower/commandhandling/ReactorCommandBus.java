package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.smallrye.mutiny.Multi;

public interface ReactorCommandBus {

    <R> Multi<R> dispatch(CommandMessage command);

    Registration subscribe(String commandName, ReactorMessageHandler<CommandMessage> handler);

}
