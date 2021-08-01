package io.iamcyw.tower.commandhandling.gateway;

import io.smallrye.mutiny.Multi;
import org.reactivestreams.Publisher;

public interface ReactorCommandGateway {

    <R> Multi<R> send(Object command);

    default Multi<Object> sendAll(Publisher<?> commands) {
        return Multi.createFrom().publisher(commands).onItem().transformToMultiAndConcatenate(this::send);
    }

}
