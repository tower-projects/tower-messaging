package io.iamcyw.tower.queryhandling.gateway;

import io.iamcyw.tower.messaging.Message;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface ReactorQueryGateway {

    default <R> Multi<R> queries(Object query) {
        return this.<List<R>>query(query).onItem().transformToMulti(collect -> Multi.createFrom().iterable(collect));
    }

    default <R> Multi<R> queries(Message queryMessage) {
        return this.<List<R>>query(queryMessage).onItem()
                   .transformToMulti(collect -> Multi.createFrom().iterable(collect));
    }

    <R> Uni<R> query(Object query);

    <R> Uni<R> query(Message queryMessage);

}
