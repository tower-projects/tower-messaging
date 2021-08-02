package io.iamcyw.tower.config;

import io.iamcyw.tower.commandhandling.AnnotationCommandHandlerInstance;
import io.iamcyw.tower.commandhandling.DefaultReactorCommandBus;
import io.iamcyw.tower.commandhandling.ReactorCommandBus;
import io.iamcyw.tower.commandhandling.gateway.DefaultReactorCommandGateway;
import io.iamcyw.tower.commandhandling.gateway.ReactorCommandGateway;
import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.queryhandling.AnnotationQueryHandlerInstance;
import io.iamcyw.tower.queryhandling.DefaultReactorQueryBus;
import io.iamcyw.tower.queryhandling.ReactorQueryBus;

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
    }

    protected ReactorQueryBus defaultQueryBus(ReactorConfiguration config) {
        return new DefaultReactorQueryBus();
    }

    protected ReactorCommandBus defaultCommandBus(ReactorConfiguration config) {
        return new DefaultReactorCommandBus();
    }

    @Override
    public void start() {
        invokeStartHandlers();
    }

    @Override
    public <C> ReactorConfigure registerComponent(Class<C> componentType,
                                                  Function<ReactorConfiguration, ? extends C> componentBuilder) {
        return null;
    }

    @Override
    public ReactorConfigure registerCommandHandler(
            Function<ReactorConfiguration, Object> annotatedCommandHandlerBuilder) {
        startHandlers.add(() -> {
            Registration registration = new AnnotationCommandHandlerInstance(
                    annotatedCommandHandlerBuilder.apply(config)).subscribe(config.commandBus());
            shutdownHandlers.add(registration::cancel);
        });
        return this;
    }

    @Override
    public ReactorConfigure registerQueryHandler(Function<ReactorConfiguration, Object> annotatedQueryHandlerBuilder) {
        startHandlers.add(() -> {
            Registration registration = new AnnotationQueryHandlerInstance(
                    annotatedQueryHandlerBuilder.apply(config)).subscribe(config.queryBus());
            shutdownHandlers.add(registration::cancel);
        });
        return this;
    }

    @Override
    public ReactorConfiguration buildConfiguration() {
        return this.config;
    }

    /**
     * Invokes all registered start handlers.
     */
    protected void invokeStartHandlers() {
        startHandlers.forEach(Runnable::run);
    }

    protected ReactorCommandGateway defaultCommandGateway(ReactorConfiguration config) {
        return new DefaultReactorCommandGateway(config.commandBus());
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
