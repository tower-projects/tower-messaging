package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Uni;

@FunctionalInterface
public interface ReactorCommandFilter {

    <C, R> Uni<R> filter(CommandMessage exchange, ReactorCommandFilterChain chain);

}
