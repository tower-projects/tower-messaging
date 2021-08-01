package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.messaging.MessageHandler;
import io.smallrye.mutiny.Multi;

public interface QueryMessageHandler<Q, R> extends MessageHandler<QueryMessage<Q, R>> {
    @Override
    public Multi<R> handle(QueryMessage<Q, R> message);

}
