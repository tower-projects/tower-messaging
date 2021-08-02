package io.iamcyw.tower.commandhandling;

import io.smallrye.mutiny.Multi;

@FunctionalInterface
public interface ReactorCommandFilterChain {

    <R> Multi<R> filter(CommandMessage commandMessage);

}
