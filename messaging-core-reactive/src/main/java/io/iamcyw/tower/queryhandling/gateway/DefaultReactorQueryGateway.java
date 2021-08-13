package io.iamcyw.tower.queryhandling.gateway;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.queryhandling.GenericQueryMessage;
import io.iamcyw.tower.queryhandling.ReactorQueryBus;
import io.smallrye.mutiny.Multi;

public class DefaultReactorQueryGateway implements ReactorQueryGateway {

    private final ReactorQueryBus queryBus;

    public DefaultReactorQueryGateway(ReactorQueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @Override
    public <R> Multi<R> query(Object query) {
        return queryBus.query(new GenericQueryMessage(query, query.getClass().getName()));
    }

    @Override
    public <R> Multi<R> query(Message queryMessage) {
        return queryBus.query(new GenericQueryMessage(queryMessage, queryMessage.getPayloadType().getName()));
    }

}
