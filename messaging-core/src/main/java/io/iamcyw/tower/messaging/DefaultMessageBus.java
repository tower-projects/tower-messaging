package io.iamcyw.tower.messaging;

import io.iamcyw.tower.Assert;
import io.iamcyw.tower.messaging.bootstrap.Bootstrap;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.interceptor.DefaultInterceptorChain;
import io.iamcyw.tower.messaging.handle.interceptor.InterceptorChain;
import io.iamcyw.tower.schema.model.OperationType;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static io.iamcyw.tower.messaging.TowerMessageServerMessages.msg;

public class DefaultMessageBus implements MessageBus {

    protected final Bootstrap bootstrap;

    public DefaultMessageBus(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public <R> CompletableFuture<R> handle(Message<R> message) {

        return DefaultInterceptorChain.buildChain(bootstrap.getMessageInterceptors(), () -> new InterceptorChain() {
            @Override
            public <R> CompletableFuture<R> filter(Message<R> msg) {
                CompletableFuture<MessageHandle<R>> messageHandleCF = msg.getMetaData().getMessageHandle();
                Assert.assertNotNull(messageHandleCF, "MessageHandle");

                return messageHandleCF.thenCompose(handle -> handle.handle(msg));
            }
        }).thenCompose(chain -> chain.filter(message));
    }

    public void route(Message message) {
        CompletableFuture<MessageHandle<?>> messageHandleCF = CompletableFuture.supplyAsync(() -> {
            Collection<MessageHandle<?>> allowHandle;
            if (message.getOperationType().equals(OperationType.QUERY)) {
                allowHandle = bootstrap.getQueryHandles().get(message.getIdentifier());
            } else if (message.getOperationType().equals(OperationType.COMMAND)) {
                allowHandle = bootstrap.getCommandHandles().get(message.getIdentifier());
            } else {
                allowHandle = Collections.emptyList();
            }
            return allowHandle.stream().filter(messageHandle -> predicateResultType(messageHandle, message))
                              .filter(messageHandle -> messageHandle.predicate(message)).findFirst()
                              .orElseThrow(() -> msg.emptyHandleException(message.getIdentifier()));
        });
        message.getMetaData().setMessageHandle(messageHandleCF);
    }

    public boolean predicateResultType(MessageHandle<?> messageHandle, Message<?> message) {
        return message.getIdentifier().equals(messageHandle.getIdentifier());
    }

    @Override
    public <R> CompletableFuture<R> dispatch(Message<R> message) {
        route(message);

        return handle(message);
    }

}
