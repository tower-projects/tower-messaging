package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Uni;

public interface ReactorCommandFilter {

    <C, R> Uni<R> filter(CommandMessage<C> exchange, ReactorCommandFilterChain chain);

}
