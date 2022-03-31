package io.iamcyw.tower.commandhandling.gateway;

import io.iamcyw.tower.responsetype.ResponseType;

public interface CommandGateway {

    @Deprecated
    <R> R request(Object command, ResponseType<R> responseType);

    void send(Object command);

}
