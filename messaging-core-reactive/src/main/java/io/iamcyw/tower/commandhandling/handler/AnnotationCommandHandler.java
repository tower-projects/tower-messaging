package io.iamcyw.tower.commandhandling.handler;

import io.iamcyw.tower.commandhandling.CommandHandler;
import io.iamcyw.tower.commandhandling.CommandMessage;
import io.iamcyw.tower.commandhandling.handler.predicate.CommandMessagePredicate;
import io.iamcyw.tower.messaging.HandlerMethod;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.smallrye.mutiny.Multi;

import java.lang.reflect.Method;

import static io.iamcyw.tower.common.annotation.AnnotationUtils.findAnnotationAttributes;

public class AnnotationCommandHandler<T> implements CommandMessageHandler<T> {

    private final CommandMessagePredicate<T> predicate;

    private final HandlerMethod handlerMethod;

    public AnnotationCommandHandler(Method method, Object target, CommandMessagePredicate<T> predicate,
                                    ParameterResolverFactory parameterResolverFactory) {
        this.predicate = predicate;

        this.handlerMethod = findAnnotationAttributes(method, CommandHandler.class).map(
                attr -> new HandlerMethod((String) attr.getOrDefault("commandName", null), method, target,
                                          (Class<?>) attr.getOrDefault("payloadType", Object.class),
                                          parameterResolverFactory)).get();
    }

    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public <R> Multi<R> handle(CommandMessage<T> message) {
        return handlerMethod.handle(message);
    }

    @Override
    public boolean canHandle(CommandMessage<T> message) {
        return predicate.test(message, handlerMethod);
    }

}
