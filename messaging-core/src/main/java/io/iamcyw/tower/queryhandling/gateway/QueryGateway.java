package io.iamcyw.tower.queryhandling.gateway;

import java.util.List;

public interface QueryGateway {

    <R> R query(Object query);

    <R> List<R> queries(Object query);

}
