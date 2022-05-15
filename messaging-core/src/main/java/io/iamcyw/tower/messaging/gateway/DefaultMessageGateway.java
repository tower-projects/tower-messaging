package io.iamcyw.tower.messaging.gateway;

import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MessageBus;
import io.iamcyw.tower.messaging.MetaData;
import io.iamcyw.tower.messaging.responsetype.ResponseType;
import io.iamcyw.tower.messaging.responsetype.ResponseTypes;
import io.iamcyw.tower.schema.model.OperationType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultMessageGateway implements MessageGateway {

    private final MessageBus messageBus;

    public DefaultMessageGateway(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    public void send(Object command) {
        sendAsync(command).join();
    }

    @Override
    public <R> List<R> queries(Object query, Class<R> response) {
        return queriesAsync(query, response).join();
    }

    @Override
    public <R> R query(Object query, Class<R> response) {
        return queryAsync(query, response).join();
    }

    @Override
    public <R> CompletableFuture<R> advance(Object payload, String command, OperationType operationType) {
        return messageBus.dispatch(
                new GenericMessage<>(payload, new MetaData(), command, ResponseTypes.anyInstanceOf(), operationType));
    }

    @Override
    public CompletableFuture<Void> sendAsync(Object command) {
        return messageBus.dispatch(wrapperMessage(command, ResponseTypes.voidInstanceOf(), OperationType.COMMAND));
    }

    @Override
    public <R> CompletableFuture<List<R>> queriesAsync(Object query, Class<R> response) {
        return messageBus.dispatch(wrapperMessage(query, ResponseTypes.listInstanceOf(response), OperationType.QUERY));
    }

    @Override
    public <R> CompletableFuture<R> queryAsync(Object query, Class<R> response) {
        return messageBus.dispatch(wrapperMessage(query, ResponseTypes.instanceOf(response), OperationType.QUERY));
    }

    private <R> Message<R> wrapperMessage(Object payload, ResponseType responseType, OperationType operationType) {
        return new GenericMessage<>(payload, responseType, operationType);
    }

}
