package io.iamcyw.tower.commandhandling.gateway;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.interceptor.MessageDispatchInterceptor;

import java.util.List;

public class DefaultCommandGateway implements CommandGateway {
    private final CommandBus commandBus;

    private final List<MessageDispatchInterceptor> interceptors;

    public DefaultCommandGateway(CommandBus commandBus, List<MessageDispatchInterceptor> interceptors) {
        this.commandBus = commandBus;
        this.interceptors = interceptors;
    }

    @Override
    public <R> R request(Object command) {
        return commandBus.dispatch(wrapperMessage(command));
    }

    @Override
    public void send(Object command) {
        commandBus.<Void>dispatch(wrapperMessage(command));
    }

    private Message wrapperMessage(Object payload) {
        Message message = new GenericMessage(payload);
        for (MessageDispatchInterceptor interceptor : interceptors) {
            message = interceptor.handle(message);
        }
        return message;
    }

}
