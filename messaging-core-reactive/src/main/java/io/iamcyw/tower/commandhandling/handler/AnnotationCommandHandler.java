package io.iamcyw.tower.commandhandling.handler;

import io.iamcyw.tower.commandhandling.AnnotationCommandHandlerInstance;
import io.iamcyw.tower.commandhandling.CommandHandler;
import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.messaging.HandlerMethod;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.predicate.MessagePredicate;
import io.iamcyw.tower.messaging.predicate.MessagePredicateFactory;
import io.smallrye.mutiny.Multi;

import java.lang.reflect.Method;

import static io.iamcyw.tower.common.annotation.AnnotationUtils.findAnnotationAttributes;

public class AnnotationCommandHandler implements ReactorMessageHandler<CommandMessage> {

    private final MessagePredicate<CommandMessage> predicate;

    private final HandlerMethod handlerMethod;

    private final AnnotationCommandHandlerInstance instance;

    public AnnotationCommandHandler(Method method, AnnotationCommandHandlerInstance instance,
                                    ParameterResolverFactory parameterResolverFactory) {
        this.predicate = MessagePredicateFactory.get(method);
        this.instance = instance;

        this.handlerMethod = findAnnotationAttributes(method, CommandHandler.class).map(
                attr -> new HandlerMethod((String) attr.getOrDefault("commandName", null), method,
                                          (Class<?>) attr.getOrDefault("payloadType", Object.class),
                                          parameterResolverFactory)).get();
    }


    public String getCommandName() {
        return handlerMethod.getCommandName();
    }

    @Override
    public boolean canHandle(CommandMessage message) {
        return predicate == null || predicate.test(message, handlerMethod);
    }

    @Override
    public <R> Multi<R> handle(CommandMessage message) {
        return handlerMethod.handle(message, instance.getCommandHandlerInstance());
    }

}
