package io.iamcyw.tower.messaging.gateway;

import java.util.List;

public interface MessageGateway {


    public void send(Object command);

    <R> List<R> queries(Object query, Class<R> response);

    <R> R query(Object query, Class<R> response);

}
