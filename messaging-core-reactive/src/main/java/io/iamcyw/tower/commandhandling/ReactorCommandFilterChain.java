package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.function.Function;

public interface ReactorCommandFilterChain {

    <C, R> Multi<R> filter(CommandMessage<C> commandMessage, Function<CommandMessage<C>, Multi<R>> target);

}
