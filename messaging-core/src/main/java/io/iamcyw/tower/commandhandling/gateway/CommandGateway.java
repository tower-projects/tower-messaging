package io.iamcyw.tower.commandhandling.gateway;

public interface CommandGateway {
    <R> R request(Object command);

    void send(Object command);

}
