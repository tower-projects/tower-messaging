package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Uni;

public interface ReactorQueryFilter {

    <Q, R> Uni<R> filter(QueryMessage query, ReactorQueryFilterChain chain);

}
