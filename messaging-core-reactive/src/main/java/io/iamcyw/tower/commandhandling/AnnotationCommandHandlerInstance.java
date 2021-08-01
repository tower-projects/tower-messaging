package io.iamcyw.tower.commandhandling;


import io.iamcyw.tower.commandhandling.handler.AnnotationCommandHandler;
import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.common.annotation.AnnotationUtils;
import io.iamcyw.tower.messaging.ReactorMessageHandler;
import io.iamcyw.tower.messaging.annotation.ClasspathParameterResolverFactory;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.utils.Assert;
import io.iamcyw.tower.utils.i18n.I18ns;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationCommandHandlerInstance implements SupportedCommandNamesAware {
    private final Object commandHandlerInstance;

    private Map<String, ReactorMessageHandler<CommandMessage<?>>> commandHandlers;

    public AnnotationCommandHandlerInstance(Object commandHandlerInstance) {
        this(commandHandlerInstance, ClasspathParameterResolverFactory.forClass(commandHandlerInstance.getClass()));
    }

    public AnnotationCommandHandlerInstance(Object commandHandlerInstance,
                                            ParameterResolverFactory parameterResolverFactory) {
        Assert.nonNull(commandHandlerInstance,
                       I18ns.create().content("commandHandlerInstance may not be null").apply());
        this.commandHandlers = initialize(commandHandlerInstance.getClass(), parameterResolverFactory);
        this.commandHandlerInstance = commandHandlerInstance;
    }

    public Map<String, ReactorMessageHandler<CommandMessage<?>>> initialize(Class<?> commandHandlerInstance,
                                                                            ParameterResolverFactory parameterResolverFactory) {
        Map<String, ReactorMessageHandler<CommandMessage<?>>> commandHandlers = new HashMap<>();

        for (Method method : commandHandlerInstance.getDeclaredMethods()) {
            if (AnnotationUtils.findAnnotation(method, CommandHandler.class) != null) {
                AnnotationCommandHandler handler = new AnnotationCommandHandler(method, this, parameterResolverFactory);
                commandHandlers.put(handler.getCommandName(), handler);
            }
        }

        return commandHandlers;
    }

    public Map<String, ReactorMessageHandler<CommandMessage<?>>> getCommandHandlers() {
        return commandHandlers;
    }

    public ReactorMessageHandler<CommandMessage<?>> getCommandHandler(String commandName) {
        return this.commandHandlers.get(commandName);
    }

    public Object getCommandHandlerInstance() {
        return commandHandlerInstance;
    }

    @Override
    public Set<String> supportedCommandNames() {
        return commandHandlers.keySet();
    }

    public Registration subscribe(ReactorCommandBus commandBus) {

        Collection<Registration> subscriptions = getCommandHandlers().keySet().stream()
                                                                     .map(commandName -> commandBus.subscribe(
                                                                             commandName,
                                                                             getCommandHandler(commandName)))
                                                                     .collect(Collectors.toList());

        return () -> subscriptions.stream().map(Registration::cancel).reduce(Boolean::logicalOr).orElse(false);
    }

}
