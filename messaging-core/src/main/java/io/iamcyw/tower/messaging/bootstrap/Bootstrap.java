package io.iamcyw.tower.messaging.bootstrap;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import io.iamcyw.tower.Assert;
import io.iamcyw.tower.messaging.DefaultMessageBus;
import io.iamcyw.tower.messaging.MessageBus;
import io.iamcyw.tower.messaging.gateway.DefaultMessageGateway;
import io.iamcyw.tower.messaging.gateway.MessageGateway;
import io.iamcyw.tower.messaging.handle.Identifier;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.interceptor.MessageInterceptor;
import io.iamcyw.tower.schema.model.Field;
import io.iamcyw.tower.schema.model.Operation;
import io.iamcyw.tower.schema.model.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Bootstrap {
    private final Schema schema;

    private MessageGateway messageGateway;

    private MessageBus messageBus;

    private Multimap<Identifier, MessageHandle<?>> queryHandles;

    private Multimap<Identifier, MessageHandle<?>> commandHandles;

    private List<MessageInterceptor> messageInterceptors = new ArrayList<>();

    public Bootstrap(Schema schema) {
        this.schema = schema;
        bootstrap();
    }

    public void bootstrap() {
        Assert.assertNotNull(schema);

        if (schema.hasQueries()) {
            Set<Operation> queries = schema.getQueries();

            queryHandles = queries.stream().map(this::newMessageHandle).collect(
                    Multimaps.toMultimap(MessageHandle::getIdentifier, messageHandle -> messageHandle,
                                         MultimapBuilder.hashKeys().hashSetValues()::build));
        }

        if (schema.hasCommands()) {
            Set<Operation> commands = schema.getCommands();

            commandHandles = commands.stream().map(this::newMessageHandle).collect(
                    Multimaps.toMultimap(MessageHandle::getIdentifier, messageHandle -> messageHandle,
                                         MultimapBuilder.hashKeys().hashSetValues()::build));
        }

        if (schema.hasPredicates()) {
            Set<Operation> predicates = schema.getPredicates();
            predicates.forEach(this::handlePredicate);
        }

    }

    public Multimap<Identifier, MessageHandle<?>> getQueryHandles() {
        return queryHandles;
    }

    public void setQueryHandles(Multimap<Identifier, MessageHandle<?>> queryHandles) {
        this.queryHandles = queryHandles;
    }

    public Multimap<Identifier, MessageHandle<?>> getCommandHandles() {
        return commandHandles;
    }

    public void setCommandHandles(Multimap<Identifier, MessageHandle<?>> commandHandles) {
        this.commandHandles = commandHandles;
    }

    public MessageBus getMessageBus() {
        if (this.messageBus == null) {
            messageBus = new DefaultMessageBus(this);
        }

        return messageBus;
    }

    public void setMessageBus(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    public List<MessageInterceptor> getMessageInterceptors() {
        return messageInterceptors;
    }

    public void setMessageInterceptors(List<MessageInterceptor> messageInterceptors) {
        this.messageInterceptors = messageInterceptors;
    }

    private MessageHandle<?> newMessageHandle(Operation query) {
        return new MessageHandle<>(query);
    }

    private void handlePredicate(Operation predicate) {
        Field domainClass = predicate.getArguments().get(0);

        if (queryHandles != null) {
            Multimaps.filterKeys(queryHandles, input -> input.getCommand().equals(domainClass)).values()
                     .forEach(messageHandle -> messageHandle.addPredicate(predicate));
        }

        if (commandHandles != null) {
            Multimaps.filterKeys(commandHandles, input -> input.getCommand().equals(domainClass)).values()
                     .forEach(messageHandle -> messageHandle.addPredicate(predicate));
        }

    }

    public MessageGateway getMessageGateway() {
        if (messageGateway == null) {
            messageGateway = new DefaultMessageGateway(getMessageBus());
        }
        return messageGateway;
    }

    public void setMessageGateway(MessageGateway messageGateway) {
        this.messageGateway = messageGateway;
    }

}
