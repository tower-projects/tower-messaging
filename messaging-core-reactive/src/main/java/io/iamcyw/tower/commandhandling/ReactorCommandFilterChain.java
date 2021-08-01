package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Uni;

import java.util.function.Function;

public interface ReactorCommandFilterChain {

    <C, R> Uni<R> filter(CommandMessage<C> commandMessage, Function<CommandMessage<C>, Uni<R>> target);

}
