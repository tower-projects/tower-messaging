package io.iamcyw.tower.messaging.gateway;

import io.iamcyw.tower.schema.model.OperationType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageGateway {

    void send(Object command);

    <R> List<R> queries(Object query, Class<R> response);

    <R> R query(Object query, Class<R> response);

    <R> CompletableFuture<R> advance(Object payload, String command, OperationType operationType);

    CompletableFuture<Void> sendAsync(Object command);

    <R> CompletableFuture<List<R>> queriesAsync(Object query, Class<R> response);

    <R> CompletableFuture<R> queryAsync(Object query, Class<R> response);

}
