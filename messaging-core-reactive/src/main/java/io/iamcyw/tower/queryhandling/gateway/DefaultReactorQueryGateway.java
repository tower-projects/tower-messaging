package io.iamcyw.tower.queryhandling.gateway;

import io.iamcyw.tower.queryhandling.GenericQueryMessage;
import io.iamcyw.tower.queryhandling.ReactorQueryBus;
import io.smallrye.mutiny.Multi;

public class DefaultReactorQueryGateway implements ReactorQueryGateway {

    private final ReactorQueryBus queryBus;

    public DefaultReactorQueryGateway(ReactorQueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @Override
    public <R> Multi<R> query(String queryName, Object query) {
        return queryBus.query(new GenericQueryMessage(query, queryName));
    }

}
