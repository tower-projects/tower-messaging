package io.iamcyw.tower.queryhandling.gateway;

import io.smallrye.mutiny.Multi;

public interface ReactorQueryGateway {

    default <R> Multi<R> query(Object query) {
        return query(query.getClass().getName(), query);
    }

    <R> Multi<R> query(String queryName, Object query);

}
