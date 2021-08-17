package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Uni;

@FunctionalInterface
public interface ReactorCommandFilterChain {

    <R> Uni<R> filter(CommandMessage commandMessage);

}
