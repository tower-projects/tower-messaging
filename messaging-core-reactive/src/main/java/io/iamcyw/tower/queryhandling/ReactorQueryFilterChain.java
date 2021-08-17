package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@FunctionalInterface
public interface ReactorQueryFilterChain {

    <R> Uni<R> filter(QueryMessage queryMessage);

}
