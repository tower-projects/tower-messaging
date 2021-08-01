package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Uni;

public interface ReactorCommandBus {

    <C, R> Uni<R> dispatch(CommandMessage<C> command);

}
