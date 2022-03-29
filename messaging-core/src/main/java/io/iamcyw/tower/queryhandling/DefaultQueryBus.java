package io.iamcyw.tower.queryhandling;

import com.google.common.collect.ImmutableList;
import io.iamcyw.tower.Async;
import io.iamcyw.tower.commandhandling.Registration;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.MessageHandlers;
import io.iamcyw.tower.messaging.interceptor.DefaultMessageHandlerInterceptorChain;
import io.iamcyw.tower.messaging.interceptor.Handle;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;
import io.iamcyw.tower.utils.Assert;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultQueryBus implements QueryBus {
    private final List<MessageHandlerInterceptor<?>> handlerInterceptors = new CopyOnWriteArrayList<>();

    private final MessageHandlers messageHandlers;

    public DefaultQueryBus(MessageHandlers messageHandlers) {
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
        ImmutableList.Builder<MessageHandlerInterceptor<R>> builder = ImmutableList.builder();
        for (MessageHandlerInterceptor<?> handlerInterceptor : handlerInterceptors) {
            if (handlerInterceptor.match(message)) {
                builder.add((MessageHandlerInterceptor<R>) handlerInterceptor);
            }
        }
        return builder.build();
    }

    private <R> Handle<R> handle() {
        return message -> {
            CompletableFuture<List<MessageHandle>> messageHandles = message.getMetaData().getMessageHandlers();
            Assert.assertNotNull(messageHandles);

            List<MessageHandle> handles = messageHandles.join();
            Assert.assertNotNull(handles);
            Assert.assertNotEmpty(handles);

            try {
                return Async.toCompletableFuture(handles.get(0).handle(message));
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }

        };
    }

}
