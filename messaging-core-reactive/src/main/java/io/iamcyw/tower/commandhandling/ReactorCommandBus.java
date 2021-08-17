package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageMethod;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ReactorCommandBus {

    <R> Uni<R> dispatch(CommandMessage command);

    Registration subscribe(String commandName, ReactorMessageMethod<CommandMessage> handler);

}
