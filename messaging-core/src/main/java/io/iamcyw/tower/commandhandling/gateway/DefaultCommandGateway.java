package io.iamcyw.tower.commandhandling.gateway;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MetaData;
import io.iamcyw.tower.messaging.interceptor.MessageDispatchInterceptor;
import io.iamcyw.tower.responsetype.ResponseType;
import io.iamcyw.tower.responsetype.ResponseTypes;

import java.util.List;

public class DefaultCommandGateway implements CommandGateway {
    private final CommandBus commandBus;

    private final List<MessageDispatchInterceptor> interceptors;

    public DefaultCommandGateway(CommandBus commandBus, List<MessageDispatchInterceptor> interceptors) {
        this.commandBus = commandBus;
        this.interceptors = interceptors;
    }

    @Override
    public <R> R request(Object command, ResponseType<R> responseType) {
        return commandBus.<R>dispatch(wrapperMessage(command, responseType)).join();
    }

    @Override
    public void send(Object command) {
        commandBus.<Void>dispatch(wrapperMessage(command, ResponseTypes.instanceOf(Void.TYPE)));
    }

    private <R> Message wrapperMessage(Object payload, ResponseType<R> responseType) {
        MetaData metaData = new MetaData();
        metaData.setResponseType(responseType);
        Message message = new GenericMessage(metaData, payload);
        for (MessageDispatchInterceptor interceptor : interceptors) {
            message = interceptor.handle(message);
        }
        return message;
    }

}
