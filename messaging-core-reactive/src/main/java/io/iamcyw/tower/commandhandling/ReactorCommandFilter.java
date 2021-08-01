package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Multi;

public interface ReactorCommandFilter {

    <C, R> Multi<R> filter(CommandMessage<C> exchange, ReactorCommandFilterChain chain);

}
