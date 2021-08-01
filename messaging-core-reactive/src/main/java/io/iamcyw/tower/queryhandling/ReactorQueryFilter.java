package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

public interface ReactorQueryFilter {

    <Q, R> Multi<QueryResponseMessage<R>> filter(QueryMessage<Q, R> query, ReactorQueryFilterChain chain);

}
