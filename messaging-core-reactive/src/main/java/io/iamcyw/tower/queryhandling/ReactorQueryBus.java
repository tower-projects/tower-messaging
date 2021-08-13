package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageMethod;
import io.smallrye.mutiny.Multi;

public interface ReactorQueryBus {

    <R> Multi<R> query(QueryMessage queryMessage);

    Registration subscribe(String queryName, ReactorMessageMethod<QueryMessage> queryHandler);

}
