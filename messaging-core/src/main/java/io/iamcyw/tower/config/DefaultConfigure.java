package io.iamcyw.tower.config;

import io.iamcyw.tower.commandhandling.AnnotationCommandHandlerAdapter;
import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.commandhandling.SimpleCommandBus;
import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.commandhandling.gateway.DefaultCommandGateway;
import io.iamcyw.tower.common.MessagingConfigurationException;
import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.common.jpa.EntityManagerProvider;
import io.iamcyw.tower.common.transaction.NoTransactionManager;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.annotation.ClasspathParameterResolverFactory;
import io.iamcyw.tower.messaging.annotation.MultiParameterResolverFactory;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.messaging.correlation.MessageOriginProvider;
import io.iamcyw.tower.messaging.interceptors.CorrelationDataInterceptor;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.queryhandling.*;
import io.iamcyw.tower.queryhandling.annotation.AnnotationQueryHandlerAdapter;
import io.iamcyw.tower.serialization.Serializer;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultConfigure implements Configure {

    private final Configuration config = new ConfigurationImpl();

    private final MessageMonitorFactoryBuilder messageMonitorFactoryBuilder = new MessageMonitorFactoryBuilder();

    private final Component<BiFunction<Class<?>, String, MessageMonitor<Message<?>>>> messageMonitorFactoryComponent
            = new Component<>(
            config, "monitorFactory", messageMonitorFactoryBuilder::build);

    private final Component<List<CorrelationDataProvider>> correlationProviders = new Component<>(config,
                                                                                                  "correlationProviders",
                                                                                                  c -> Collections
                                                                                                          .singletonList(
                                                                                                                  new MessageOriginProvider()));

    private final Map<Class<?>, Component<?>> components = new HashMap<>();

    private final Component<Serializer> eventSerializer = new Component<>(config, "eventSerializer",
                                                                          Configuration::messageSerializer);

    private final Component<Serializer> messageSerializer = new Component<>(config, "messageSerializer",
                                                                            Configuration::serializer);

    private final List<Consumer<Configuration>> initHandlers = new ArrayList<>();

    private final List<Runnable> startHandlers = new ArrayList<>();

    private final List<Runnable> shutdownHandlers = new ArrayList<>();

    private final List<ModuleConfiguration> modules = new ArrayList<>();

    private boolean initialized = false;

    /**
     * Initialize the Configure.
     */
    public DefaultConfigure() {
        components.put(ParameterResolverFactory.class,
                       new Component<>(config, "parameterResolverFactory", this::defaultParameterResolverFactory));
        components.put(Serializer.class, new Component<>(config, "serializer", this::defaultSerializer));
        components.put(CommandBus.class, new Component<>(config, "commandBus", this::defaultCommandBus));
        components.put(CommandGateway.class, new Component<>(config, "commandGateway", this::defaultCommandGateway));
        components.put(QueryBus.class, new Component<>(config, "queryBus", this::defaultQueryBus));
        components.put(QueryUpdateEmitter.class,
                       new Component<>(config, "queryUpdateEmitter", this::defaultQueryUpdateEmitter));
        components.put(QueryGateway.class, new Component<>(config, "queryGateway", this::defaultQueryGateway));
    }

    /**
     * Returns a Configure instance with default components configured, such as a {@link SimpleCommandBus} and
     * {@link SimpleEventBus}.
     *
     * @return Configure instance for further configuration.
     */
    public static Configure defaultConfiguration() {
        return new DefaultConfigure();
    }

    public static Configure jpaConfiguration(EntityManagerProvider entityManagerProvider,
                                             TransactionManager transactionManager) {
        return new DefaultConfigure().registerComponent(EntityManagerProvider.class, c -> entityManagerProvider)
                                     .registerComponent(TransactionManager.class, c -> transactionManager);
    }

    /**
     * Returns a {@link DefaultCommandGateway} that will use the configuration's {@link CommandBus} to dispatch
     * commands.
     *
     * @param config The configuration that supplies the command bus.
     * @return The default command gateway.
     */
    protected CommandGateway defaultCommandGateway(Configuration config) {
        return new DefaultCommandGateway(config.commandBus());
    }

    /**
     * Returns a {@link DefaultQueryGateway} that will use the configuration's {@link QueryBus} to dispatch queries.
     *
     * @param config The configuration that supplies the query bus.
     * @return The default query gateway.
     */
    protected QueryGateway defaultQueryGateway(Configuration config) {
        return new DefaultQueryGateway(config.queryBus());
    }

    /**
     * Provides the default QueryBus implementations. Subclasses may override this method to provide their own default.
     *
     * @param config The configuration based on which the component is initialized.
     * @return The default QueryBus to use.
     */
    protected QueryBus defaultQueryBus(Configuration config) {
        return new SimpleQueryBus(config.messageMonitor(SimpleQueryBus.class, "queryBus"),
                                  config.messageMonitor(QueryUpdateEmitter.class, "queryUpdateEmitter"),
                                  config.getComponent(TransactionManager.class, NoTransactionManager::instance),
                                  config.getComponent(QueryInvocationErrorHandler.class));
    }

    /**
     * Provides the default QueryUpdateEmitter implementation. Subclasses may override this method to provide their own
     * default.
     *
     * @param config The configuration based on which the component is initialized
     * @return The default QueryUpdateEmitter to use
     */
    protected QueryUpdateEmitter defaultQueryUpdateEmitter(Configuration config) {
        QueryBus queryBus = config.getComponent(QueryBus.class);
        if (!(queryBus instanceof QueryUpdateEmitter)) {
            throw new MessagingConfigurationException(
                    "Implementation of query bus does not provide emitting functionality. Provide a query update " +
                            "emitter or query bus which supports emitting.");
        }
        return (QueryUpdateEmitter) queryBus;
    }

    /**
     * Provides the default ParameterResolverFactory. Subclasses may override this method to provide their own default.
     *
     * @param config The configuration based on which the component is initialized.
     * @return The default ParameterResolverFactory to use.
     */
    protected ParameterResolverFactory defaultParameterResolverFactory(Configuration config) {
        return MultiParameterResolverFactory.ordered(ClasspathParameterResolverFactory.forClass(getClass()),
                                                     new ConfigurationParameterResolverFactory(config));
    }

    /**
     * Provides the default CommandBus implementation. Subclasses may override this method to provide their own default.
     *
     * @param config The configuration based on which the component is initialized.
     * @return The default CommandBus to use.
     */
    protected CommandBus defaultCommandBus(Configuration config) {
        SimpleCommandBus cb = new SimpleCommandBus(
                config.getComponent(TransactionManager.class, () -> NoTransactionManager.INSTANCE),
                config.messageMonitor(SimpleCommandBus.class, "commandBus"));
        cb.registerHandlerInterceptor(new CorrelationDataInterceptor<>(config.correlationDataProviders()));
        return cb;
    }

    /**
     * Provides the default Serializer implementation. Subclasses may override this method to provide their own default.
     *
     * @param config The configuration based on which the component is initialized.
     * @return The default Serializer to use.
     */
    protected Serializer defaultSerializer(Configuration config) {
        // return new XStreamSerializer(config.getComponent(RevisionResolver.class, AnnotationRevisionResolver::new));
        return null;
    }

    @Override
    public Configure configureMessageMonitor(
            Function<Configuration, BiFunction<Class<?>, String, MessageMonitor<Message<?>>>> builder) {
        messageMonitorFactoryBuilder.add((conf, type, name) -> builder.apply(conf).apply(type, name));
        return this;
    }

    @Override
    public Configure configureMessageMonitor(Class<?> componentType, MessageMonitorFactory messageMonitorFactory) {
        messageMonitorFactoryBuilder.add(componentType, messageMonitorFactory);
        return this;
    }

    @Override
    public Configure configureMessageMonitor(Class<?> componentType, String componentName,
                                             MessageMonitorFactory messageMonitorFactory) {
        messageMonitorFactoryBuilder.add(componentType, componentName, messageMonitorFactory);
        return this;
    }

    @Override
    public Configure configureCorrelationDataProviders(
            Function<Configuration, List<CorrelationDataProvider>> correlationDataProviderBuilder) {
        correlationProviders.update(correlationDataProviderBuilder);
        return this;
    }

    @Override
    public Configure registerModule(ModuleConfiguration module) {
        if (initialized) {
            module.initialize(config);
        } else {
            initHandlers.add(module::initialize);
        }
        this.modules.add(module);
        startHandlers.add(module::start);
        shutdownHandlers.add(module::shutdown);
        return this;
    }

    @Override
    public Configure registerCommandHandler(Function<Configuration, Object> annotatedCommandHandlerBuilder) {
        startHandlers.add(() -> {
            Registration registration = new AnnotationCommandHandlerAdapter(
                    annotatedCommandHandlerBuilder.apply(config), config.parameterResolverFactory())
                    .subscribe(config.commandBus());
            shutdownHandlers.add(registration::cancel);
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Configure registerQueryHandler(Function<Configuration, Object> annotatedQueryHandlerBuilder) {
        startHandlers.add(() -> {
            Registration registration = new AnnotationQueryHandlerAdapter(annotatedQueryHandlerBuilder.apply(config),
                                                                          config.parameterResolverFactory())
                    .subscribe(config.queryBus());
            shutdownHandlers.add(registration::cancel);
        });
        return this;
    }

    @Override
    public <C> Configure registerComponent(Class<C> componentType,
                                           Function<Configuration, ? extends C> componentBuilder) {
        components.put(componentType, new Component<>(config, componentType.getSimpleName(), componentBuilder));
        return this;
    }


    @Override
    public Configure configureEventSerializer(Function<Configuration, Serializer> eventSerializerBuilder) {
        eventSerializer.update(eventSerializerBuilder);
        return this;
    }

    @Override
    public Configure configureMessageSerializer(Function<Configuration, Serializer> messageSerializerBuilder) {
        messageSerializer.update(messageSerializerBuilder);
        return this;
    }

    @Override
    public Configuration buildConfiguration() {
        if (!initialized) {
            invokeInitHandlers();
        }
        return config;
    }

    /**
     * Calls all registered init handlers. Registration of init handlers after this invocation will result in an
     * immediate invocation of that handler.
     */
    protected void invokeInitHandlers() {
        initialized = true;
        initHandlers.forEach(h -> h.accept(config));
    }

    /**
     * Invokes all registered start handlers.
     */
    protected void invokeStartHandlers() {
        startHandlers.forEach(Runnable::run);
    }

    /**
     * Invokes all registered shutdown handlers.
     */
    protected void invokeShutdownHandlers() {
        shutdownHandlers.forEach(Runnable::run);
    }

    /**
     * Returns the current Configuration object being built by this Configure, without initializing it. Note that
     * retrieving objects from this configuration may lead to premature initialization of certain components.
     *
     * @return The current Configuration object being built by this Configure.
     */
    protected Configuration getConfig() {
        return config;
    }

    /**
     * Returns a map of all registered components in this configuration. The key of the map is the registered component
     * type (typically an interface), the value is a Component instance that wraps the actual implementation. Note that
     * calling {@link Component#get()} may prematurely initialize a component.
     *
     * @return A map of all registered components in this configuration.
     */
    public Map<Class<?>, Component<?>> getComponents() {
        return components;
    }

    private class ConfigurationImpl implements Configuration {

        @Override
        public <T> T getComponent(Class<T> componentType, Supplier<T> defaultImpl) {
            return componentType.cast(components.computeIfAbsent(componentType, k -> new Component<>(config,
                                                                                                     componentType
                                                                                                             .getSimpleName(),
                                                                                                     c -> defaultImpl
                                                                                                             .get()))
                                                .get());
        }

        @Override
        public <M extends Message<?>> MessageMonitor<? super M> messageMonitor(Class<?> componentType,
                                                                               String componentName) {
            return messageMonitorFactoryComponent.get().apply(componentType, componentName);
        }

        @Override
        public Serializer messageSerializer() {
            return messageSerializer.get();
        }

        @Override
        public void start() {
            invokeStartHandlers();
        }

        @Override
        public void shutdown() {
            invokeShutdownHandlers();
        }

        @Override
        public List<CorrelationDataProvider> correlationDataProviders() {
            return correlationProviders.get();
        }

        @Override
        public List<ModuleConfiguration> getModules() {
            return modules;
        }

        @Override
        public void onShutdown(Runnable shutdownHandler) {
            shutdownHandlers.add(shutdownHandler);
        }

        @Override
        public void onStart(Runnable startHandler) {
            startHandlers.add(startHandler);
        }

    }

}
