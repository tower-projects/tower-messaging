package io.iamcyw.tower.queryhandling.gateway;

public interface QueryGateway {

    <R> R query(Object query);

}
