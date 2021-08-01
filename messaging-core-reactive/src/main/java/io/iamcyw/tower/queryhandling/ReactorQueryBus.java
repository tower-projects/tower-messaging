package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

import java.util.concurrent.TimeUnit;

public interface ReactorQueryBus {

    <Q, R> Multi<QueryResponseMessage<R>> query(QueryMessage<Q, R> queryMessage);

    <Q, R> Multi<QueryResponseMessage<R>> scatterGather(QueryMessage<Q, R> queryMessage, long timeout, TimeUnit unit);

}
