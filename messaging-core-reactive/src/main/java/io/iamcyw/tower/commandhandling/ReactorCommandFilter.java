package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Multi;

@FunctionalInterface
public interface ReactorCommandFilter {

    <C, R> Multi<R> filter(CommandMessage exchange, ReactorCommandFilterChain chain);

}
