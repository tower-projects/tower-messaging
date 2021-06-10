package io.iamcyw.tower.config;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.annotation.HandlerDefinition;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.serialization.Serializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DefaultConfigure implements Configure {


    public static Configure defaultConfiguration() {
        return defaultConfiguration(true);
    }

    public static Configure defaultConfiguration(boolean autoLocateConfigurerModules) {
        DefaultConfigure configure = new DefaultConfigure();
        if (autoLocateConfigurerModules) {
            ServiceLoader<ConfigureModule> configurerModuleLoader = ServiceLoader.load(ConfigureModule.class,
                                                                                       configure.getClass()
                                                                                               .getClassLoader());
            List<ConfigureModule> configurerModules = new ArrayList<>();
            configurerModuleLoader.forEach(configurerModules::add);
            configurerModules.sort(Comparator.comparingInt(ConfigureModule::order));
            configurerModules.forEach(cm -> cm.configureModule(configure));
        }
        return configure;
    }

    @Override
    public Configure configureMessageMonitor(Function<Configuration, BiFunction<Class<?>, String, MessageMonitor<Message<?>>>> messageMonitorFactoryBuilder) {
        return null;
    }

    @Override
    public Configure configureMessageMonitor(Class<?> componentType, MessageMonitorFactory messageMonitorFactory) {
        return null;
    }

    @Override
    public Configure configureMessageMonitor(Class<?> componentType, String componentName, MessageMonitorFactory messageMonitorFactory) {
        return null;
    }

    @Override
    public Configure configureCorrelationDataProviders(Function<Configuration, List<CorrelationDataProvider>> correlationDataProviderBuilder) {
        return null;
    }

    @Override
    public <C> Configure registerComponent(Class<C> componentType, Function<Configuration, ? extends C> componentBuilder) {
        return null;
    }

    @Override
    public Configure registerCommandHandler(Function<Configuration, Object> commandHandlerBuilder) {
        return null;
    }

    @Override
    public Configure registerQueryHandler(Function<Configuration, Object> queryHandlerBuilder) {
        return null;
    }

    @Override
    public Configure registerMessageHandler(Function<Configuration, Object> messageHandlerBuilder) {
        return null;
    }

    @Override
    public Configure configureEventSerializer(Function<Configuration, Serializer> eventSerializerBuilder) {
        return null;
    }

    @Override
    public Configure configureMessageSerializer(Function<Configuration, Serializer> messageSerializerBuilder) {
        return null;
    }

    @Override
    public Configure registerModule(ModuleConfiguration module) {
        return null;
    }

    @Override
    public Configure registerHandlerDefinition(BiFunction<Configuration, Class, HandlerDefinition> handlerDefinitionClass) {
        return null;
    }

    @Override
    public Configuration buildConfiguration() {
        return null;
    }

}
