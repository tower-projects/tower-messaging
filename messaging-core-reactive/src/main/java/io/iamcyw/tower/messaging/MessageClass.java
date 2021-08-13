package io.iamcyw.tower.messaging;

import io.iamcyw.tower.commandhandling.ReactorCommandBus;
import io.iamcyw.tower.commandhandling.SupportedCommandNamesAware;
import io.iamcyw.tower.commandhandling.handler.CommandMessageMethod;
import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.queryhandling.ReactorQueryBus;
import io.iamcyw.tower.queryhandling.handler.QueryMessageMethod;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MessageClass implements SupportedCommandNamesAware {
    private final Map<String, ReactorMessageMethod<?>> handlers = new ConcurrentHashMap<>();

    private BeanFactory<?> beanInstance;

    private volatile Object instance;

    public Map<String, ReactorMessageMethod<? extends Message>> getHandlers() {
        return handlers;
    }

    public ReactorMessageMethod<? extends Message> getHandler(String name) {
        return handlers.get(name);
    }

    public Registration subscribe(ReactorCommandBus commandBus) {

        Collection<Registration> subscriptions = getHandlers().values().stream()
                                                              .filter(method -> method instanceof CommandMessageMethod)
                                                              .map(commandMethod -> commandBus.subscribe(
                                                                      commandMethod.getName(),
                                                                      (CommandMessageMethod) commandMethod))
                                                              .collect(Collectors.toList());

        return () -> subscriptions.stream().map(Registration::cancel).reduce(Boolean::logicalOr).orElse(false);
    }

    public Registration subscribe(ReactorQueryBus queryBus) {

        Collection<Registration> subscriptions = getHandlers().values().stream()
                                                              .filter(method -> method instanceof QueryMessageMethod)
                                                              .map(method -> queryBus.subscribe(method.getName(),
                                                                                                (QueryMessageMethod) method))
                                                              .collect(Collectors.toList());

        return () -> subscriptions.stream().map(Registration::cancel).reduce(Boolean::logicalOr).orElse(false);
    }

    public void setBeanInstance(BeanFactory<?> beanInstance) {
        this.beanInstance = beanInstance;
    }

    @Override
    public Set<String> supportedCommandNames() {
        return handlers.keySet();
    }

    public Object getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = beanInstance.createInstance().getInstance();
                }
            }
        }
        return instance;
    }

}
