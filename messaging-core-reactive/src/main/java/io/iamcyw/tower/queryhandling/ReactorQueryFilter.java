package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

public interface ReactorQueryFilter {

    <Q, R> Multi<R> filter(QueryMessage query, ReactorQueryFilterChain chain);

}
