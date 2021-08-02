package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.smallrye.mutiny.Multi;

public interface ReactorQueryBus {

    <R> Multi<R> query(QueryMessage queryMessage);

    Registration subscribe(String queryName, ReactorMessageHandler<QueryMessage> queryHandler);

}
