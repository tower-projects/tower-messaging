package io.iamcyw.tower.config;

import io.iamcyw.tower.commandhandling.DefaultReactorCommandBus;
import io.iamcyw.tower.commandhandling.ReactorCommandBus;
import io.iamcyw.tower.commandhandling.gateway.DefaultReactorCommandGateway;
import io.iamcyw.tower.commandhandling.gateway.ReactorCommandGateway;
import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.MessageClass;
import io.iamcyw.tower.queryhandling.DefaultReactorQueryBus;
import io.iamcyw.tower.queryhandling.ReactorQueryBus;
import io.iamcyw.tower.queryhandling.gateway.DefaultReactorQueryGateway;
import io.iamcyw.tower.queryhandling.gateway.ReactorQueryGateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultReactorConfigure implements ReactorConfigure {

    private final ReactorConfiguration config = new ConfigurationImpl();

    private final Map<Class<?>, ReactorComponent<?>> components = new HashMap<>();

    private final List<Runnable> startHandlers = new ArrayList<>();

    private final List<Runnable> shutdownHandlers = new ArrayList<>();

    public DefaultReactorConfigure() {
        components.put(ReactorCommandBus.class, new ReactorComponent<>(config, "commandBus", this::defaultCommandBus));
        components.put(ReactorCommandGateway.class,
                       new ReactorComponent<>(config, "commandGateway", this::defaultCommandGateway));
        components.put(ReactorQueryBus.class, new ReactorComponent<>(config, "queryBus", this::defaultQueryBus));
        components.put(ReactorQueryGateway.class,
                       new ReactorComponent<>(config, "queryGateway", this::defaultQueryGateway));
    }

    public static ReactorConfigure defaultConfiguration() {
        return new DefaultReactorConfigure();
    }

    protected ReactorQueryBus defaultQueryBus(ReactorConfiguration config) {
        return new DefaultReactorQueryBus();
    }

    protected ReactorCommandBus defaultCommandBus(ReactorConfiguration config) {
        return new DefaultReactorCommandBus();
    }

    protected ReactorQueryGateway defaultQueryGateway(ReactorConfiguration config) {
        return new DefaultReactorQueryGateway(config.queryBus());
    }

    protected ReactorCommandGateway defaultCommandGateway(ReactorConfiguration config) {
        return new DefaultReactorCommandGateway(config.commandBus());
    }

    @Override
    public void start() {
        invokeStartHandlers();
    }

    @Override
    public <C> ReactorConfigure registerComponent(Class<C> componentType,
                                                  Function<ReactorConfiguration, ? extends C> componentBuilder) {
        components.put(componentType, new ReactorComponent<>(config, componentType.getSimpleName(), componentBuilder));
        return this;
    }

    @Override
    public ReactorConfigure registerCommandHandler(Function<ReactorConfiguration, MessageClass> commandClassBuilder) {
        startHandlers.add(() -> {
            Registration registration = commandClassBuilder.apply(config).subscribe(config.commandBus());
            shutdownHandlers.add(registration::cancel);
        });
        return this;
    }

    @Override
    public ReactorConfiguration buildConfiguration() {
        return this.config;
    }

    @Override
    public ReactorConfigure registerQueryHandler(
            Function<ReactorConfiguration, MessageClass> annotatedQueryHandlerBuilder) {
        startHandlers.add(() -> {
            Registration registration = annotatedQueryHandlerBuilder.apply(config).subscribe(config.queryBus());
            shutdownHandlers.add(registration::cancel);
        });
        return this;
    }

    /**
     * Invokes all registered start handlers.
     */
    protected void invokeStartHandlers() {
        startHandlers.forEach(Runnable::run);
    }

    private class ConfigurationImpl implements ReactorConfiguration {

        @Override
        public <T> T getComponent(Class<T> componentType, Supplier<T> defaultImpl) {
            return componentType.cast(components.computeIfAbsent(componentType, k -> new ReactorComponent<>(config,
                                                                                                            componentType.getSimpleName(),
                                                                                                            c -> defaultImpl.get()))
                                                .get());
        }


    }

}
