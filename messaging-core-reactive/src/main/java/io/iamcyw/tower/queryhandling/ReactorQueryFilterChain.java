package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

import java.util.function.Function;

public interface ReactorQueryFilterChain {

    <Q, R> Multi<QueryResponseMessage<R>> filter(QueryMessage<Q, R> queryMessage,
                                                 Function<QueryMessage<Q, R>, Multi<QueryResponseMessage<R>>> target);

}
