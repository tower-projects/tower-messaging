package io.iamcyw.tower.messaging.gateway;

import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MessageBus;
import io.iamcyw.tower.messaging.responsetype.ResponseType;
import io.iamcyw.tower.messaging.responsetype.ResponseTypes;
import io.iamcyw.tower.schema.model.OperationType;

import java.util.List;

public class DefaultMessageGateway implements MessageGateway {

    private final MessageBus messageBus;

    public DefaultMessageGateway(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    public void send(Object command) {
        messageBus.dispatch(wrapperMessage(command, ResponseTypes.voidInstanceOf(), OperationType.COMMAND)).join();
    }

    @Override
    public <R> List<R> queries(Object query, Class<R> response) {
        return messageBus.dispatch(
                wrapperMessage(query, ResponseTypes.multipleInstancesOf(response), OperationType.QUERY)).join();
    }

    @Override
    public <R> R query(Object query, Class<R> response) {
        return messageBus.dispatch(wrapperMessage(query, ResponseTypes.instanceOf(response), OperationType.QUERY))
                         .join();
    }

    private <R> Message<R> wrapperMessage(Object payload, ResponseType<R> responseType, OperationType operationType) {
        Message<R> message = new GenericMessage<R>(payload, responseType, operationType);
        return message;
    }

}
