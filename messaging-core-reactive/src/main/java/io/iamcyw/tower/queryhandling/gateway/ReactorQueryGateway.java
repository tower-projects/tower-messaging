package io.iamcyw.tower.queryhandling.gateway;

import io.iamcyw.tower.messaging.Message;
import io.smallrye.mutiny.Multi;

public interface ReactorQueryGateway {

    <R> Multi<R> query(Object query);

    <R> Multi<R> query(Message queryMessage);

}
