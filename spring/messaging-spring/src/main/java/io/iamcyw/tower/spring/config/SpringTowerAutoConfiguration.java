package io.iamcyw.tower.spring.config;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.config.*;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryUpdateEmitter;
import io.iamcyw.tower.serialization.Serializer;
import io.iamcyw.tower.spring.config.annotation.SpringContextParameterResolverFactoryBuilder;
import io.iamcyw.tower.spring.messaging.unitofwork.SpringTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.iamcyw.tower.spring.SpringUtils.isQualifierMatch;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class SpringTowerAutoConfiguration implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

    /**
     * Name of the {@link TowerConfiguration} bean.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String TOWER_CONFIGURATION_BEAN = "io.iamcyw.tower.spring.config.TowerConfiguration";

    /**
     * Name of the {@link Configure} bean.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String TOWER_CONFIGURE_BEAN = "io.iamcyw.tower.config.Configure";


    private static final Logger logger = LoggerFactory.getLogger(SpringTowerAutoConfiguration.class);

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition("commandHandlerSubscriber",
                                        genericBeanDefinition(CommandHandlerSubscriber.class).getBeanDefinition());

        registry.registerBeanDefinition("queryHandlerSubscriber",
                                        genericBeanDefinition(QueryHandlerSubscriber.class).getBeanDefinition());

        Configure configurer = DefaultConfigure.defaultConfiguration();

        RuntimeBeanReference parameterResolver = SpringContextParameterResolverFactoryBuilder
                .getBeanReference(registry);
        configurer.registerComponent(ParameterResolverFactory.class, c -> beanFactory
                .getBean(parameterResolver.getBeanName(), ParameterResolverFactory.class));

        findComponent(CommandBus.class)
                .ifPresent(commandBus -> configurer.configureCommandBus(c -> getBean(commandBus, c)));
        findComponent(QueryBus.class).ifPresent(queryBus -> configurer.configureQueryBus(c -> getBean(queryBus, c)));
        findComponent(QueryUpdateEmitter.class).ifPresent(
                queryUpdateEmitter -> configurer.configureQueryUpdateEmitter(c -> getBean(queryUpdateEmitter, c)));
        findComponent(Serializer.class)
                .ifPresent(serializer -> configurer.configureSerializer(c -> getBean(serializer, c)));
        findComponent(Serializer.class, "eventSerializer")
                .ifPresent(eventSerializer -> configurer.configureEventSerializer(c -> getBean(eventSerializer, c)));
        findComponent(Serializer.class, "messageSerializer").ifPresent(
                messageSerializer -> configurer.configureMessageSerializer(c -> getBean(messageSerializer, c)));
        try {
            findComponent(PlatformTransactionManager.class).ifPresent(
                    ptm -> configurer.configureTransactionManager(c -> new SpringTransactionManager(getBean(ptm, c))));
        } catch (NoClassDefFoundError error) {
            // that's fine...
        }
        findComponent(TransactionManager.class)
                .ifPresent(tm -> configurer.configureTransactionManager(c -> getBean(tm, c)));
        // findComponent(ListenerInvocationErrorHandler.class).ifPresent(handler -> configurer
        //         .registerComponent(ListenerInvocationErrorHandler.class, c -> getBean(handler, c)));
        // findComponent(ErrorHandler.class)
        //         .ifPresent(handler -> configurer.registerComponent(ErrorHandler.class, c -> getBean(handler, c)));

        // String resourceInjector = findComponent(ResourceInjector.class, registry,
        //                                         () -> genericBeanDefinition(SpringResourceInjector.class)
        //                                                 .getBeanDefinition());
        // configurer.configureResourceInjector(c -> getBean(resourceInjector, c));

        registerCorrelationDataProviders(configurer);
        // registerAggregateBeanDefinitions(configurer, registry);
        registerModules(configurer);

        beanFactory.registerSingleton(TOWER_CONFIGURE_BEAN, configurer);
        registry.registerBeanDefinition(TOWER_CONFIGURATION_BEAN, genericBeanDefinition(TowerConfiguration.class)
                .addConstructorArgReference(TOWER_CONFIGURE_BEAN).getBeanDefinition());
        // registerEventHandlerRegistrar(ehConfigBeanName, registry);
    }

    private void registerCorrelationDataProviders(Configure configurer) {
        configurer.configureCorrelationDataProviders(c -> {
            String[] correlationDataProviderBeans = beanFactory.getBeanNamesForType(CorrelationDataProvider.class);
            return Arrays.stream(correlationDataProviderBeans).map(n -> (CorrelationDataProvider) getBean(n, c))
                         .collect(Collectors.toList());
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T getBean(String beanName, Configuration configuration) {
        return (T) configuration.getComponent(ApplicationContext.class).getBean(beanName);
    }

    // private void registerEventHandlerRegistrar(String ehConfigBeanName, BeanDefinitionRegistry registry) {
    //     List<RuntimeBeanReference> beans = new ManagedList<>();
    //     beanFactory.getBeanNamesIterator().forEachRemaining(bean -> {
    //         if (!beanFactory.isFactoryBean(bean)) {
    //             Class<?> beanType = beanFactory.getType(bean);
    //             if (beanType != null && beanFactory.containsBeanDefinition(bean) &&
    //                     beanFactory.getBeanDefinition(bean).isSingleton()) {
    //                 boolean hasHandler = StreamSupport.stream(methodsOf(beanType).spliterator(), false)
    //                                                   .map(m -> findAnnotationAttributes(m, MessageHandler.class)
    //                                                           .orElse(null)).filter(Objects::nonNull).anyMatch(
    //                                 attr -> EventMessage.class.isAssignableFrom((Class) attr.get("messageType")));
    //                 if (hasHandler) {
    //                     beans.add(new RuntimeBeanReference(bean));
    //                 }
    //             }
    //         }
    //     });
    //     registry.registerBeanDefinition("eventHandlerRegistrar", genericBeanDefinition(EventHandlerRegistrar.class)
    //             .addConstructorArgReference(TOWER_CONFIGURATION_BEAN).addConstructorArgReference(ehConfigBeanName)
    //             .addPropertyValue("eventHandlers", beans).getBeanDefinition());
    // }

    private void registerModules(Configure configurer) {
        registerConfigurerModules(configurer);
        registerModuleConfigurations(configurer);
    }

    private void registerConfigurerModules(Configure configurer) {
        String[] configurerModules = beanFactory.getBeanNamesForType(ConfigureModule.class);
        for (String configurerModuleBeanName : configurerModules) {
            ConfigureModule configureModule = beanFactory.getBean(configurerModuleBeanName, ConfigureModule.class);
            configureModule.configureModule(configurer);
        }
    }

    private void registerModuleConfigurations(Configure configurer) {
        String[] moduleConfigurations = beanFactory.getBeanNamesForType(ModuleConfiguration.class);
        for (String moduleConfiguration : moduleConfigurations) {
            configurer.registerModule(new LazyRetrievedModuleConfiguration(
                    () -> beanFactory.getBean(moduleConfiguration, ModuleConfiguration.class)));
        }
    }

    // @SuppressWarnings("unchecked")
    // private void registerAggregateBeanDefinitions(Configure configurer, BeanDefinitionRegistry registry) {
    //     String[] aggregates = beanFactory.getBeanNamesForAnnotation(Aggregate.class);
    //     for (String aggregate : aggregates) {
    //         Aggregate aggregateAnnotation = beanFactory.findAnnotationOnBean(aggregate, Aggregate.class);
    //         Class<?> aggregateType = beanFactory.getType(aggregate);
    //         AggregateConfigurer<?> aggregateConf = AggregateConfigurer.defaultConfiguration(aggregateType);
    //         if ("".equals(aggregateAnnotation.repository())) {
    //             String repositoryName = lcFirst(aggregateType.getSimpleName()) + "Repository";
    //             String factoryName =
    //                     aggregate.substring(0, 1).toLowerCase() + aggregate.substring(1) + "AggregateFactory";
    //             if (beanFactory.containsBean(repositoryName)) {
    //                 aggregateConf.configureRepository(c -> beanFactory.getBean(repositoryName, Repository.class));
    //             } else {
    //                 if (!registry.isBeanNameInUse(factoryName)) {
    //                     registry.registerBeanDefinition(factoryName,
    //                                                     genericBeanDefinition(SpringPrototypeAggregateFactory.class)
    //                                                             .addPropertyValue("prototypeBeanName", aggregate)
    //                                                             .getBeanDefinition());
    //                 }
    //                 aggregateConf
    //                         .configureAggregateFactory(c -> beanFactory.getBean(factoryName, AggregateFactory
    //                         .class));
    //                 String triggerDefinition = aggregateAnnotation.snapshotTriggerDefinition();
    //                 if (!"".equals(triggerDefinition)) {
    //                     aggregateConf.configureSnapshotTrigger(
    //                             c -> beanFactory.getBean(triggerDefinition, SnapshotTriggerDefinition.class));
    //                 }
    //                 if (AnnotationUtils.findAnnotation(aggregateType, "javax.persistence.Entity") != null) {
    //                     aggregateConf.configureRepository(c -> new GenericJpaRepository(
    //                             c.getComponent(EntityManagerProvider.class,
    //                                            () -> beanFactory.getBean(EntityManagerProvider.class)),
    //                                            aggregateType,
    //                             c.eventBus(), c.getComponent(LockFactory.class, () -> NullLockFactory.INSTANCE),
    //                             c.parameterResolverFactory()));
    //                 }
    //             }
    //         } else {
    //             aggregateConf.configureRepository(
    //                     c -> beanFactory.getBean(aggregateAnnotation.repository(), Repository.class));
    //         }
    //
    //         if (!"".equals(aggregateAnnotation.commandTargetResolver())) {
    //             aggregateConf.configureCommandTargetResolver(c -> beanFactory
    //                     .getBean(aggregateAnnotation.commandTargetResolver(), CommandTargetResolver.class));
    //         }
    //
    //         configurer.configureAggregate(aggregateConf);
    //     }
    // }

    /**
     * Return the given {@code string}, with its first character lowercase
     *
     * @param string The input string
     * @return The input string, with first character lowercase
     */
    private String lcFirst(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    private <T> String findComponent(Class<T> componentType, BeanDefinitionRegistry registry,
                                     Supplier<BeanDefinition> defaultBean) {
        return findComponent(componentType).orElseGet(() -> {
            BeanDefinition beanDefinition = defaultBean.get();
            String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
            registry.registerBeanDefinition(beanName, beanDefinition);
            return beanName;
        });
    }

    private <T> Optional<String> findComponent(Class<T> componentType, String componentQualifier) {
        return Stream.of(beanFactory.getBeanNamesForType(componentType))
                     .filter(bean -> isQualifierMatch(bean, beanFactory, componentQualifier)).findFirst();
    }

    private <T> Optional<String> findComponent(Class<T> componentType) {
        String[] beans = beanFactory.getBeanNamesForType(componentType);
        if (beans.length == 1) {
            return Optional.of(beans[0]);
        } else if (beans.length > 1) {
            for (String bean : beans) {
                BeanDefinition beanDef = beanFactory.getBeanDefinition(bean);
                if (beanDef.isPrimary()) {
                    return Optional.of(bean);
                }
            }
            logger.warn("Multiple beans of type {} found in application context: {}. Chose {}",
                        componentType.getSimpleName(), beans, beans[0]);
            return Optional.of(beans[0]);
        }
        return Optional.empty();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    /**
     * Implementation of an {@link ImportSelector} that enables the import of the
     * {@link SpringTowerAutoConfiguration} after
     * all {@code @Configuration} beans have been processed.
     */
    public static class ImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{SpringTowerAutoConfiguration.class.getName()};
        }

    }

    private static class LazyRetrievedModuleConfiguration implements ModuleConfiguration {

        private final Supplier<ModuleConfiguration> delegateSupplier;

        private ModuleConfiguration delegate;

        LazyRetrievedModuleConfiguration(Supplier<ModuleConfiguration> delegateSupplier) {
            this.delegateSupplier = delegateSupplier;
        }

        @Override
        public void initialize(Configuration config) {
            delegate = delegateSupplier.get();
            delegate.initialize(config);
        }

        @Override
        public void start() {
            delegate.start();
        }

        @Override
        public void shutdown() {
            delegate.shutdown();
        }

    }

}
