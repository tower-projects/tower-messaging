package io.iamcyw.tower.commandhandling.gateway;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.reactivestreams.Publisher;

public interface ReactorCommandGateway {

    <R> Uni<R> send(Object command);

    default Multi<Object> sendAll(Publisher<?> commands) {
        return Multi.createFrom().publisher(commands).onItem().transformToUniAndConcatenate(this::send);
    }

}
