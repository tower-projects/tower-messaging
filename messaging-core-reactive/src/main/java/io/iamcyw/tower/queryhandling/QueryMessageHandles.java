package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

public interface QueryMessageHandles {

    <Q, R> Multi<QueryMessageHandler<Q, R>> getHandles(QueryMessage<Q, R> queryMessage);

}
