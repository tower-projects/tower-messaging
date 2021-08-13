package io.iamcyw.tower.messaging;

import io.smallrye.mutiny.Multi;

public interface EndpointInvoker {

    <R> Multi<R> invoke(Object instance, Object[] args);

}
