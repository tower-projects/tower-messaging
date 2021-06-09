package io.iamcyw.tower.spring.config;

import io.iamcyw.tower.config.Configuration;
import io.iamcyw.tower.config.Configurer;
import io.iamcyw.tower.config.LifecycleHandler;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.annotation.HandlerDefinition;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.serialization.Serializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.util.List;
import java.util.function.Supplier;

public class TowerConfiguration implements Configuration, InitializingBean, ApplicationContextAware, SmartLifecycle {

    private final Configurer configurer;

    private Configuration config;

    private volatile boolean running = false;

    public TowerConfiguration(Configurer configurer) {
        this.configurer = configurer;
    }

    @Override
    public <T> T getComponent(Class<T> componentType, Supplier<T> defaultImpl) {
        return config.getComponent(componentType, defaultImpl);
    }

    @Override
    public <M extends Message<?>> MessageMonitor<? super M> messageMonitor(Class<?> componentType, String componentName) {
        return config.messageMonitor(componentType, componentName);
    }

    @Override
    public Serializer messageSerializer() {
        return null;
    }

    @Override
    public void start() {
        config.start();
        this.running = true;
    }

    @Override
    public void stop() {
        shutdown();
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void shutdown() {
        config.shutdown();
    }

    @Override
    public List<CorrelationDataProvider> correlationDataProviders() {
        return config.correlationDataProviders();
    }

    @Override
    public HandlerDefinition handlerDefinition(Class<?> inspectedType) {
        return config.handlerDefinition(inspectedType);
    }

    @Override
    public void onStart(int phase, LifecycleHandler startHandler) {
        config.onStart(phase, startHandler);
    }

    @Override
    public void onShutdown(int phase, LifecycleHandler shutdownHandler) {
        config.onShutdown(phase, shutdownHandler);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config = configurer.buildConfiguration();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        configurer.registerComponent(ApplicationContext.class, c -> applicationContext);
    }

}
