package io.iamcyw.tower.config;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryGateway;
import io.iamcyw.tower.queryhandling.QueryUpdateEmitter;
import io.iamcyw.tower.serialization.Serializer;

import java.util.List;
import java.util.function.Supplier;

/**
 * Interface describing the Global Configuration for Axon components. It provides access to the components configured,
 * such as the Command Bus and Event Bus.
 * <p>
 * Note that certain components in the Configuration may need to be started. Therefore, before using any of the
 * components provided by this configuration, ensure that {@link #start()} has been invoked.
 */
public interface Configuration {


    /**
     * Returns the Command Bus defined in this Configuration. Note that this Configuration should be started (see
     * {@link #start()}) before sending Commands over the Command Bus.
     *
     * @return the CommandBus defined in this configuration
     */
    default CommandBus commandBus() {
        return getComponent(CommandBus.class);
    }

    default QueryBus queryBus() {
        return getComponent(QueryBus.class);
    }

    /**
     * Returns the Query Update Emitter in this Configuration. Note that this Configuration should be started (see
     * {@link #start()} before emitting updates over Query Update Emitter.
     *
     * @return the QueryUpdateEmitter defined in this configuration
     */
    default QueryUpdateEmitter queryUpdateEmitter() {
        return getComponent(QueryUpdateEmitter.class);
    }


    /**
     * Returns the Command Gateway defined in this Configuration. Note that this Configuration should be started (see
     * {@link #start()}) before sending Commands using this Command Gateway.
     *
     * @return the CommandGateway defined in this configuration
     */
    default CommandGateway commandGateway() {
        return getComponent(CommandGateway.class);
    }

    /**
     * Returns the Query Gateway defined in this Configuration. Note that this Configuration should be started (see
     * {@link #start()}) before sending Queries using this Query Gateway.
     *
     * @return the QueryGateway defined in this configuration
     */
    default QueryGateway queryGateway() {
        return getComponent(QueryGateway.class);
    }

    /**
     * Returns the Component declared under the given {@code componentType}, typically the interface the component
     * implements.
     *
     * @param componentType The type of component
     * @param <T>           The type of component
     * @return the component registered for the given type, or {@code null} if no such component exists
     */
    default <T> T getComponent(Class<T> componentType) {
        return getComponent(componentType, () -> null);
    }

    /**
     * Returns the Component declared under the given {@code componentType}, typically the interface the component
     * implements, reverting to the given {@code defaultImpl} if no such component is defined.
     * <p>
     * When no component was previously registered, the default is then configured as the component for the given type.
     *
     * @param componentType The type of component
     * @param defaultImpl   The supplier of the default to return if no component was registered
     * @param <T>           The type of component
     * @return the component registered for the given type, or the value returned by the {@code defaultImpl} supplier,
     * if no component was registered
     */
    <T> T getComponent(Class<T> componentType, Supplier<T> defaultImpl);

    /**
     * Returns the message monitor configured for a component of given {@code componentType} and {@code componentName}.
     *
     * @param componentType The type of component to return the monitor for
     * @param componentName The name of the component
     * @param <M>           The type of message the monitor can deal with
     * @return The monitor to be used for the described component
     */
    <M extends Message<?>> MessageMonitor<? super M> messageMonitor(Class<?> componentType, String componentName);

    /**
     * Returns the serializer defined in this Configuration
     *
     * @return the serializer defined in this Configuration
     */
    default Serializer serializer() {
        return getComponent(Serializer.class);
    }

    /**
     * Returns the {@link Serializer} defined in this Configuration to be used for serializing Message payloads and
     * metadata.
     *
     * @return the message serializer defined in this Configuration.
     */
    Serializer messageSerializer();

    /**
     * Starts this configuration. All components defined in this Configuration will be started.
     */
    void start();

    /**
     * Shuts down the components defined in this Configuration
     */
    void shutdown();

    /**
     * Returns the Correlation Data Providers defined in this Configuration.
     *
     * @return the Correlation Data Providers defined in this Configuration
     */
    List<CorrelationDataProvider> correlationDataProviders();

    /**
     * Returns the Parameter Resolver Factory defined in this Configuration
     *
     * @return the Parameter Resolver Factory defined in this Configuration
     */
    default ParameterResolverFactory parameterResolverFactory() {
        return getComponent(ParameterResolverFactory.class);
    }

    /**
     * Returns all modules that have been registered with this Configuration.
     *
     * @return all modules that have been registered with this Configuration
     */
    List<ModuleConfiguration> getModules();

    /**
     * Registers a handler to be executed when this Configuration is started.
     * <p>
     * The behavior for handlers that are registered when the Configuration is already started is undefined.
     *
     * @param startHandler The handler to execute when the configuration is started
     * @see #start()
     * @see #onShutdown(Runnable)
     */
    void onStart(Runnable startHandler);

    /**
     * Registers a handler to be executed when the Configuration is shut down.
     * <p>
     * The behavior for handlers that are registered when the Configuration is already shut down is undefined.
     *
     * @param shutdownHandler The handler to execute when the Configuration is shut down
     * @see #shutdown()
     * @see #onStart(Runnable)
     */
    void onShutdown(Runnable shutdownHandler);

}
