package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.ReactorMessageMethod;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ReactorQueryBus {

    <R> Uni<R> query(QueryMessage queryMessage);

    Registration subscribe(String queryName, ReactorMessageMethod<QueryMessage> queryHandler);

}
