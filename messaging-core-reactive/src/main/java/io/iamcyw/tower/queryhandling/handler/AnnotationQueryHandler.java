package io.iamcyw.tower.queryhandling.handler;

import io.iamcyw.tower.messaging.HandlerMethod;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.predicate.MessagePredicate;
import io.iamcyw.tower.messaging.predicate.MessagePredicateFactory;
import io.iamcyw.tower.queryhandling.AnnotationQueryHandlerInstance;
import io.iamcyw.tower.queryhandling.QueryHandler;
import io.iamcyw.tower.queryhandling.QueryMessage;
import io.smallrye.mutiny.Multi;

import java.lang.reflect.Method;

import static io.iamcyw.tower.common.annotation.AnnotationUtils.findAnnotationAttributes;

public class AnnotationQueryHandler<T> implements ReactorMessageHandler<QueryMessage> {

    private final MessagePredicate<QueryMessage> predicate;

    private final HandlerMethod handlerMethod;

    private final AnnotationQueryHandlerInstance instance;

    public AnnotationQueryHandler(Method method, AnnotationQueryHandlerInstance instance,
                                  ParameterResolverFactory parameterResolverFactory) {
        this.predicate = MessagePredicateFactory.get(method);
        this.instance = instance;

        this.handlerMethod = findAnnotationAttributes(method, QueryHandler.class).map(
                attr -> new HandlerMethod((String) attr.getOrDefault("commandName", null), method,
                                          (Class<?>) attr.getOrDefault("payloadType", Object.class),
                                          parameterResolverFactory)).get();
    }

    @Override
    public boolean canHandle(QueryMessage message) {
        return predicate == null || predicate.test(message, handlerMethod);
    }

    @Override
    public <R> Multi<R> handle(QueryMessage message) {
        return handlerMethod.handle(message, instance.getCommandHandlerInstance());
    }

    public String getQueryName() {
        return handlerMethod.getCommandName();
    }

}
