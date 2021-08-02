package io.iamcyw.tower.queryhandling;

import io.smallrye.mutiny.Multi;

public interface ReactorQueryFilterChain {

    <Q, R> Multi<R> filter(QueryMessage queryMessage);

}
