package io.iamcyw.tower.spring.config;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.config.Configuration;
import io.iamcyw.tower.config.Configure;
import io.iamcyw.tower.config.DefaultConfigure;
import io.iamcyw.tower.config.ModuleConfiguration;
import io.iamcyw.tower.messaging.annotation.HandlerDefinition;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryUpdateEmitter;
import io.iamcyw.tower.serialization.Serializer;
import io.iamcyw.tower.spring.ConfigurerFactoryBean;
import io.iamcyw.tower.spring.config.annotation.SpringContextHandlerDefinitionBuilder;
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
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.iamcyw.tower.spring.SpringUtils.isQualifierMatch;
import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
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
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Configure configure = DefaultConfigure.defaultConfiguration(false);

        RuntimeBeanReference parameterResolver = SpringContextParameterResolverFactoryBuilder.getBeanReference(
                registry);
        configure.registerComponent(ParameterResolverFactory.class,
                                    c -> beanFactory.getBean(parameterResolver.getBeanName(),
                                                             ParameterResolverFactory.class));

        RuntimeBeanReference handlerDefinition = SpringContextHandlerDefinitionBuilder.getBeanReference(registry);
        configure.registerHandlerDefinition(
                (c, clazz) -> beanFactory.getBean(handlerDefinition.getBeanName(), HandlerDefinition.class));

        registerComponent(CommandBus.class, configure::configureCommandBus, configure, Configuration::commandBus);
        registerComponent(QueryBus.class, configure::configureQueryBus, configure, Configuration::queryBus);
        registerComponent(QueryUpdateEmitter.class, configure::configureQueryUpdateEmitter);
        registerComponent(Serializer.class, configure::configureSerializer);
        registerComponent(Serializer.class, "eventSerializer", configure::configureEventSerializer);
        registerComponent(Serializer.class, "messageSerializer", configure::configureMessageSerializer);
        try {
            findComponent(PlatformTransactionManager.class).ifPresent(
                    ptm -> configure.configureTransactionManager(c -> new SpringTransactionManager(getBean(ptm, c))));
        } catch (NoClassDefFoundError error) {
            // that's fine...
        }
        registerComponent(TransactionManager.class, configure::configureTransactionManager);

        registerModuleConfigurations(configure);
        registerCorrelationDataProviders(configure);

        registry.registerBeanDefinition(TOWER_CONFIGURE_BEAN,
                                        genericBeanDefinition(ConfigurerFactoryBean.class).addConstructorArgValue(
                                                configure)
                                                .getBeanDefinition());
        registry.registerBeanDefinition(TOWER_CONFIGURATION_BEAN,
                                        genericBeanDefinition(TowerConfiguration.class).addConstructorArgReference(
                                                TOWER_CONFIGURE_BEAN)
                                                .getBeanDefinition());
    }


    /**
     * Register a component of {@code componentType} through the given {@code registrationFunction}. The component to
     * register will be a bean retrieved from the {@link ApplicationContext} tied to the {@link Configuration}.
     *
     * @param componentType        the type of the component to register
     * @param registrationFunction the function to register the component to the {@link Configuration}
     * @param <T>                  the type of the component
     */
    private <T> void registerComponent(Class<T> componentType, Consumer<Function<Configuration, T>> registrationFunction) {
        findComponent(componentType).ifPresent(
                componentName -> registrationFunction.accept(config -> getBean(componentName, config)));
    }

    private void registerCorrelationDataProviders(Configure configure) {
        configure.configureCorrelationDataProviders(c -> {
            String[] correlationDataProviderBeans = beanFactory.getBeanNamesForType(CorrelationDataProvider.class);
            return Arrays.stream(correlationDataProviderBeans)
                    .map(n -> (CorrelationDataProvider) getBean(n, c))
                    .collect(Collectors.toList());
        });
    }

    private void registerModuleConfigurations(Configure configure) {
        String[] moduleConfigurations = beanFactory.getBeanNamesForType(ModuleConfiguration.class);
        for (String moduleConfiguration : moduleConfigurations) {
            configure.registerModule(new LazyRetrievedModuleConfiguration(
                    () -> beanFactory.getBean(moduleConfiguration, ModuleConfiguration.class),
                    beanFactory.getType(moduleConfiguration)));
        }
    }

    /**
     * Register a component of {@code componentType} with {@code componentQualifier} through the given {@code
     * registrationFunction}. The component to register will be a bean retrieved from the {@link ApplicationContext}
     * tied to the {@link Configuration}.
     *
     * @param componentType        the type of the component to register
     * @param componentQualifier   the qualifier of the component to register
     * @param registrationFunction the function to register the component to the {@link Configuration}
     * @param <T>                  the type of the component
     */
    private <T> void registerComponent(Class<T> componentType, String componentQualifier, Consumer<Function<Configuration, T>> registrationFunction) {
        findComponent(componentType, componentQualifier).ifPresent(
                componentName -> registrationFunction.accept(config -> getBean(componentName, config)));
    }

    private <T> Optional<String> findComponent(Class<T> componentType, String componentQualifier) {
        return Stream.of(beanNamesForTypeIncludingAncestors(beanFactory, componentType))
                .filter(bean -> isQualifierMatch(bean, beanFactory, componentQualifier))
                .findFirst();
    }


    /**
     * Register a component of {@code componentType} with the given {@code configurer} through {@link
     * Configure#registerComponent(Class, Function)}. The component to register will be a bean retrieved from the
     * {@link ApplicationContext} tied to the {@link Configuration}.
     *
     * @param componentType the type of the component to register
     * @param configure     the {@link Configure} used to register the component with
     * @param <T>           the type of the component
     */
    private <T> void registerComponent(Class<T> componentType, Configure configure) {
        registerComponent(componentType, builder -> configure.registerComponent(componentType, builder), configure,
                          null);
    }

    /**
     * Register a component of {@code componentType} through the given {@code registrationFunction}. The {@code
     * initHandler} is used to initialize the component at the right point in time. The component to register will be a
     * bean retrieved from the {@link ApplicationContext} tied to the {@link Configuration}.
     *
     * @param componentType        the type of the component to register
     * @param registrationFunction the function to register the component to the {@link Configuration}
     * @param configure            the {@link Configure} used to register the component with
     * @param initHandler          the function used to initialize the registered component
     * @param <T>                  the type of the component
     */
    private <T> void registerComponent(Class<T> componentType, Consumer<Function<Configuration, T>> registrationFunction, Configure configure, Consumer<Configuration> initHandler) {
        findComponent(componentType).ifPresent(componentName -> {
            registrationFunction.accept(config -> getBean(componentName, config));
            if (initHandler != null) {
                configure.onInitialize(c -> c.onStart(Integer.MIN_VALUE, () -> initHandler.accept(c)));
            }
        });
    }

    private <T> Optional<String> findComponent(Class<T> componentType) {
        String[] beans = beanNamesForTypeIncludingAncestors(beanFactory, componentType);
        if (beans.length == 1) {
            return Optional.of(beans[0]);
        } else if (beans.length > 1) {
            for (String bean : beans) {
                BeanDefinition beanDef = beanFactory.getMergedBeanDefinition(bean);
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

    @SuppressWarnings("unchecked")
    private <T> T getBean(String beanName, Configuration configuration) {
        return (T) configuration.getComponent(ApplicationContext.class)
                .getBean(beanName);
    }

    public static class ImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{SpringTowerAutoConfiguration.class.getName()};
        }

    }

    private static class LazyRetrievedModuleConfiguration implements ModuleConfiguration {

        private final Supplier<ModuleConfiguration> delegateSupplier;

        private final Class<?> moduleType;

        private ModuleConfiguration delegate;

        LazyRetrievedModuleConfiguration(Supplier<ModuleConfiguration> delegateSupplier, Class<?> moduleType) {
            this.delegateSupplier = delegateSupplier;
            this.moduleType = moduleType;
        }

        @Override
        public void initialize(Configuration config) {
            getDelegate().initialize(config);
        }

        @Override
        public ModuleConfiguration unwrap() {
            return getDelegate();
        }

        @Override
        public boolean isType(Class<?> type) {
            return type.isAssignableFrom(moduleType);
        }

        private ModuleConfiguration getDelegate() {
            if (delegate == null) {
                delegate = delegateSupplier.get();
            }
            return delegate;
        }

    }

}
