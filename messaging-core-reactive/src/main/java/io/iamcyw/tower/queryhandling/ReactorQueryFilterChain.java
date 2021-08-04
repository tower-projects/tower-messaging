package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

@FunctionalInterface
public interface ReactorQueryFilterChain {

    <R> Multi<R> filter(QueryMessage queryMessage);

}
