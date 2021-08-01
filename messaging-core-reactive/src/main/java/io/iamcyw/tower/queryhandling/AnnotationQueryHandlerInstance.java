package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.commandhandling.SupportedCommandNamesAware;
import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.common.annotation.AnnotationUtils;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.iamcyw.tower.messaging.annotation.ClasspathParameterResolverFactory;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.queryhandling.handler.AnnotationQueryHandler;
import io.iamcyw.tower.utils.Assert;
import io.iamcyw.tower.utils.i18n.I18ns;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationQueryHandlerInstance implements SupportedCommandNamesAware {

    private final Object commandHandlerInstance;

    private final Map<String, ReactorMessageHandler<QueryMessage<?, ?>>> queryHandles;

    public AnnotationQueryHandlerInstance(Object commandHandlerInstance) {
        this(commandHandlerInstance, ClasspathParameterResolverFactory.forClass(commandHandlerInstance.getClass()));
    }

    public AnnotationQueryHandlerInstance(Object commandHandlerInstance,
                                          ParameterResolverFactory parameterResolverFactory) {
        Assert.nonNull(commandHandlerInstance,
                       I18ns.create().content("commandHandlerInstance may not be null").apply());
        this.queryHandles = initialize(commandHandlerInstance.getClass(), parameterResolverFactory);
        this.commandHandlerInstance = commandHandlerInstance;
    }

    public Map<String, ReactorMessageHandler<QueryMessage<?, ?>>> initialize(Class<?> commandHandlerInstance,
                                                                             ParameterResolverFactory parameterResolverFactory) {
        Map<String, ReactorMessageHandler<QueryMessage<?, ?>>> queryHandles = new HashMap<>();

        for (Method method : commandHandlerInstance.getDeclaredMethods()) {
            if (AnnotationUtils.findAnnotation(method, QueryHandler.class) != null) {
                AnnotationQueryHandler handler = new AnnotationQueryHandler(method, this, parameterResolverFactory);
                queryHandles.put(handler.getQueryName(), handler);
            }
        }

        return queryHandles;
    }

    public Map<String, ReactorMessageHandler<QueryMessage<?, ?>>> getQueryHandles() {
        return queryHandles;
    }

    public ReactorMessageHandler<QueryMessage<?, ?>> getQueryHandler(String commandName) {
        return this.queryHandles.get(commandName);
    }

    public Object getCommandHandlerInstance() {
        return commandHandlerInstance;
    }

    @Override
    public Set<String> supportedCommandNames() {
        return queryHandles.keySet();
    }

    public Registration subscribe(ReactorQueryBus queryBus) {

        Collection<Registration> subscriptions = getQueryHandles().keySet().stream()
                                                                  .map(commandName -> queryBus.subscribe(commandName,
                                                                                                         getQueryHandler(
                                                                                                                 commandName)))
                                                                  .collect(Collectors.toList());

        return () -> subscriptions.stream().map(Registration::cancel).reduce(Boolean::logicalOr).orElse(false);
    }

}
