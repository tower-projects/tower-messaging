package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.MessageHandlers;
import io.iamcyw.tower.messaging.interceptor.DefaultMessageHandlerInterceptorChain;
import io.iamcyw.tower.messaging.interceptor.Handle;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class DefaultCommandBus implements CommandBus {
    private final List<MessageHandlerInterceptor> handlerInterceptors = new CopyOnWriteArrayList<>();

    private final MessageHandlers messageHandlers;

    private final Handle<?> handle = new DefaultHandle<>();

    public DefaultCommandBus(MessageHandlers messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    @Override
    public <R> R dispatch(Message message) {
        message.getMetaData().setMessageHandlers(allowMessageHandlers(message));
        return filter(message);
    }

    @Override
    public Registration registerHandlerInterceptor(MessageHandlerInterceptor messageHandlerInterceptor) {
        handlerInterceptors.add(messageHandlerInterceptor);
        return () -> handlerInterceptors.remove(messageHandlerInterceptor);
    }

    private Stream<MessageHandle> allowMessageHandlers(Message message) {
        return messageHandlers.get(message);
    }

    private <R> R filter(Message message) {
        return DefaultMessageHandlerInterceptorChain.buildChain(handlerInterceptors, handle).filter(message);
    }

}
