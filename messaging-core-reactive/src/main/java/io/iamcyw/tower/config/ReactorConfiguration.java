package io.iamcyw.tower.config;

import io.iamcyw.tower.commandhandling.ReactorCommandBus;
import io.iamcyw.tower.commandhandling.gateway.ReactorCommandGateway;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.queryhandling.ReactorQueryBus;
import io.iamcyw.tower.queryhandling.gateway.ReactorQueryGateway;

import java.util.function.Supplier;

public interface ReactorConfiguration {

    default ReactorCommandBus commandBus() {
        return getComponent(ReactorCommandBus.class);
    }

    default ReactorQueryBus queryBus() {
        return getComponent(ReactorQueryBus.class);
    }

    default ReactorCommandGateway commandGateway() {
        return getComponent(ReactorCommandGateway.class);
    }

    default ReactorQueryGateway queryGateway() {
        return getComponent(ReactorQueryGateway.class);
    }

    default <T> T getComponent(Class<T> componentType) {
        return getComponent(componentType, () -> null);
    }

    default ParameterResolverFactory parameterResolverFactory() {
        return getComponent(ParameterResolverFactory.class);
    }

    <T> T getComponent(Class<T> componentType, Supplier<T> defaultImpl);

}
