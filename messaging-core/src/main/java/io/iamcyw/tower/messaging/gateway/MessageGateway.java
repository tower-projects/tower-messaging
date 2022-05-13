package io.iamcyw.tower.messaging.gateway;

import io.iamcyw.tower.schema.model.WrapperType;

import java.util.List;

public interface MessageGateway {

    public void send(Object command);

    <R> List<R> queries(Object query, Class<R> response);

    <R> R query(Object query, Class<R> response);

    Object query(Object query, String responseClass, WrapperType wrapperType);

}
