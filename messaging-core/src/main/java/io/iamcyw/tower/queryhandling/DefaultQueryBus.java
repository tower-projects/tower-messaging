package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.commandhandling.DefaultHandle;
import io.iamcyw.tower.commandhandling.Registration;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.MessageHandlers;
import io.iamcyw.tower.messaging.interceptor.DefaultMessageHandlerInterceptorChain;
import io.iamcyw.tower.messaging.interceptor.Handle;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class DefaultQueryBus implements QueryBus {
    private final MessageHandlers messageHandlers;

    private final Handle<?> handle = new DefaultHandle<>();

    private final List<MessageHandlerInterceptor> interceptors = new CopyOnWriteArrayList<>();

    public DefaultQueryBus(MessageHandlers messageHandlers) {
        this.messageHandlers = messageHandlers;
    }


    @Override
    public <R> R dispatch(Message message) {
        message.getMetaData().setMessageHandlers(allowMessageHandlers(message));
        return filter(message);
    }

    @Override
    public Registration registerHandlerInterceptor(MessageHandlerInterceptor messageHandlerInterceptor) {
        interceptors.add(messageHandlerInterceptor);
        return () -> interceptors.remove(messageHandlerInterceptor);
    }

    private Stream<MessageHandle> allowMessageHandlers(Message message) {
        return messageHandlers.get(message);
    }

    private <R> R filter(Message message) {
        return DefaultMessageHandlerInterceptorChain.buildChain(interceptors, handle).filter(message);
    }

}
