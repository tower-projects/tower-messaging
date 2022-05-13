package io.iamcyw.tower.messaging;

import com.google.common.collect.Multimap;
import io.iamcyw.tower.Assert;
import io.iamcyw.tower.messaging.handle.Identifier;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.schema.model.OperationType;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static io.iamcyw.tower.messaging.TowerMessageServerMessages.msg;

public class DefaultMessageBus implements MessageBus {
    private final Multimap<Identifier, MessageHandle<?>> queries;

    private final Multimap<Identifier, MessageHandle<?>> commands;

    public DefaultMessageBus(Multimap<Identifier, MessageHandle<?>> queries,
                             Multimap<Identifier, MessageHandle<?>> commands) {
        this.queries = queries;
        this.commands = commands;
    }

    private <R> CompletableFuture<R> handle(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            CompletableFuture<MessageHandle<R>> messageHandleCF = message.getMetaData().getMessageHandle();
            Assert.assertNotNull(messageHandleCF, "MessageHandle");
            MessageHandle<R> messageHandle = messageHandleCF.join();
            Assert.assertNotNull(messageHandle, "MessageHandle");

            return messageHandle;
        }).thenCompose(msgHandle -> msgHandle.handle(message));
    }

    private void route(Message message) {
        CompletableFuture<MessageHandle<?>> messageHandleCF = CompletableFuture.supplyAsync(() -> {
            Collection<MessageHandle<?>> allowHandle;
            if (message.getOperationType().equals(OperationType.QUERY)) {
                allowHandle = queries.get(message.getIdentifier());
            } else if (message.getOperationType().equals(OperationType.COMMAND)) {
                allowHandle = commands.get(message.getIdentifier());
            } else {
                allowHandle = Collections.emptyList();
            }
            return allowHandle.stream().filter(messageHandle -> predicateResultType(messageHandle, message))
                              .filter(messageHandle -> messageHandle.predicate(message)).findFirst()
                              .orElseThrow(() -> msg.emptyHandleException(message.getIdentifier()));
        });
        message.getMetaData().setMessageHandle(messageHandleCF);
    }

    private boolean predicateResultType(MessageHandle<?> messageHandle, Message<?> message) {
        return message.getIdentifier().equals(messageHandle.getIdentifier());
    }

    @Override
    public <R> CompletableFuture<R> dispatch(Message<R> message) {
        route(message);

        return handle(message);
    }

}
