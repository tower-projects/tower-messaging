package io.iamcyw.tower.spring.config;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.commandhandling.gateway.DefaultCommandGateway;
import io.iamcyw.tower.config.Configuration;
import io.iamcyw.tower.config.Configure;
import io.iamcyw.tower.config.ModuleConfiguration;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.queryhandling.DefaultQueryGateway;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryGateway;
import io.iamcyw.tower.queryhandling.QueryUpdateEmitter;
import io.iamcyw.tower.serialization.Serializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.function.Supplier;

@org.springframework.context.annotation.Configuration("io.iamcyw.tower.spring.config.TowerConfiguration")
public class TowerConfiguration implements Configuration, InitializingBean, ApplicationContextAware, SmartLifecycle {

    private final Configure configurer;

    private Configuration config;

    private volatile boolean running = false;

    /**
     * Initializes a new {@link TowerConfiguration} that uses the given {@code configurer} to build the configuration.
     *
     * @param configurer Configuration builder for the AxonConfiguration.
     */
    public TowerConfiguration(Configure configurer) {
        this.configurer = configurer;
    }

    @Override
    public CommandBus commandBus() {
        return config.commandBus();
    }

    @Override
    public QueryBus queryBus() {
        return config.queryBus();
    }

    @Override
    public QueryUpdateEmitter queryUpdateEmitter() {
        return config.queryUpdateEmitter();
    }

    @NoBeanOfType(QueryBus.class)
    @Bean("queryBus")
    @Primary
    public QueryBus defaultQueryBus() {
        return config.queryBus();
    }

    @NoBeanOfType(QueryUpdateEmitter.class)
    @Bean("queryUpdateEmitter")
    public QueryUpdateEmitter defaultQueryUpdateEmitter() {
        return config.queryUpdateEmitter();
    }

    @NoBeanOfType(CommandBus.class)
    @Bean("commandBus")
    public CommandBus defaultCommandBus() {
        return commandBus();
    }

    /**
     * Returns the CommandGateway used to send commands to command handlers.
     *
     * @param commandBus the command bus to be used by the gateway
     * @return the CommandGateway used to send commands to command handlers
     */
    @NoBeanOfType(CommandGateway.class)
    @Bean
    public CommandGateway commandGateway(CommandBus commandBus) {
        return new DefaultCommandGateway(commandBus);
    }

    @NoBeanOfType(QueryGateway.class)
    @Bean
    public QueryGateway queryGateway(QueryBus queryBus) {
        return new DefaultQueryGateway(queryBus);
    }

    @Override
    public <T> T getComponent(Class<T> componentType, Supplier<T> defaultImpl) {
        return config.getComponent(componentType, defaultImpl);
    }

    @Override
    public <M extends Message<?>> MessageMonitor<? super M> messageMonitor(Class<?> componentType,
                                                                           String componentName) {
        return config.messageMonitor(componentType, componentName);
    }

    @Override
    public Serializer messageSerializer() {
        return config.messageSerializer();
    }

    @Override
    public List<CorrelationDataProvider> correlationDataProviders() {
        return config.correlationDataProviders();
    }

    @Override
    public List<ModuleConfiguration> getModules() {
        return config.getModules();
    }

    @Override
    public void onStart(Runnable startHandler) {
        config.onStart(startHandler);
    }

    @Override
    public void onShutdown(Runnable shutdownHandler) {
        config.onShutdown(shutdownHandler);
    }

    @Override
    public void start() {
        config.start();
        this.running = true;
    }

    @Override
    public void shutdown() {
        config.shutdown();
    }

    @Override
    public void stop() {
        shutdown();
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }


    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void afterPropertiesSet() {
        config = configurer.buildConfiguration();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        configurer.registerComponent(ApplicationContext.class, c -> applicationContext);
    }

}
