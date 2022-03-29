package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.Async;
import io.iamcyw.tower.exception.Errors;
import io.iamcyw.tower.exception.MessageIllegalStateException;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.MessageHandlers;
import io.iamcyw.tower.messaging.interceptor.DefaultMessageHandlerInterceptorChain;
import io.iamcyw.tower.messaging.interceptor.Handle;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;
import io.iamcyw.tower.utils.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultCommandBus implements CommandBus {
    private final List<MessageHandlerInterceptor<?>> handlerInterceptors = new CopyOnWriteArrayList<>();

    private final MessageHandlers messageHandlers;

    public DefaultCommandBus(MessageHandlers messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    @Override
    public <R> CompletableFuture<R> dispatch(Message message) {
        message.getMetaData().setMessageHandlers(allowMessageHandlers(message));
        return filter(message);
    }

    @Override
    public Registration registerHandlerInterceptor(MessageHandlerInterceptor<?> messageHandlerInterceptor) {
        handlerInterceptors.add(messageHandlerInterceptor);
        return () -> handlerInterceptors.remove(messageHandlerInterceptor);
    }

    private CompletableFuture<List<MessageHandle>> allowMessageHandlers(Message message) {
        return messageHandlers.get(message);
    }

    private <R> CompletableFuture<R> filter(Message message) {
        return DefaultMessageHandlerInterceptorChain.<R>buildChain(matchInterceptor(message), handle())
                                                    .thenComposeAsync(chain -> chain.filter(message));
    }

    private <R> List<MessageHandlerInterceptor<R>> matchInterceptor(Message message) {
        List<MessageHandlerInterceptor<R>> interceptors = new ArrayList<>();
        for (MessageHandlerInterceptor<?> handlerInterceptor : handlerInterceptors) {
            if (handlerInterceptor.match(message)) {
                interceptors.add((MessageHandlerInterceptor<R>) handlerInterceptor);
            }
        }
        return interceptors;
    }

    private <R> Handle<R> handle() {
        return message -> {
            CompletableFuture<List<MessageHandle>> messageHandles = message.getMetaData().getMessageHandlers();
            Assert.assertNotNull(messageHandles);

            List<MessageHandle> handles = messageHandles.join();
            Assert.assertNotEmpty(handles, () -> {
                throw new MessageIllegalStateException(Errors.create().content("the command not any handler").apply());
            });

            try {
                return Async.toCompletableFuture(handles.get(0).handle(message));
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }

        };
    }

}
