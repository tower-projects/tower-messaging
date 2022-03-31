package io.iamcyw.tower.queryhandling.gateway;

import io.iamcyw.tower.responsetype.ResponseType;

public interface QueryGateway {

    <R> R query(Object query, ResponseType<R> responseType);

}
