/*
 * Copyright (c) 2010-2020. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.iamcyw.tower.config;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.commandhandling.SimpleCommandBus;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryUpdateEmitter;
import io.iamcyw.tower.serialization.Serializer;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Configure {


    /**
     * Configures the Message Monitor to use for the Message processing components in this configuration, unless more
     * specific configuration based on the component's type, or type and name is available. The builder function
     * receives the type of component as well as its name as input, and is expected to return a MessageMonitor
     * instance to be used by that type of component.
     *
     * @param messageMonitorFactoryBuilder The MessageMonitor builder function
     * @return the current instance of the Configure, for chaining purposes
     */
    Configure configureMessageMonitor(
            Function<Configuration, BiFunction<Class<?>, String, MessageMonitor<Message<?>>>> messageMonitorFactoryBuilder);

    default Configure configureMessageMonitor(Class<?> componentType,
                                              Function<Configuration, MessageMonitor<Message<?>>> messageMonitorBuilder) {
        return configureMessageMonitor(componentType,
                                       (configuration, type, name) -> messageMonitorBuilder.apply(configuration));
    }

    /**
     * Configures the factory to create the Message Monitor for the Message processing components in this configuration
     * that match the given componentType, unless more specific configuration based on both type and name is available.
     * <p>
     * <p>A component matches componentType if componentType is assignable from the component's class. If a component
     * matches multiple types, and the types derive from each other, the configuration from the most derived type is
     * used. If the matching types do not derive from each other, the result is unspecified.</p>
     * <p>
     * <p>For example: in case a monitor is configured for {@link CommandBus} and another monitor is configured for
     * {@link SimpleCommandBus SimpleCommandBus}), components of type
     * {@link AsynchronousCommandBus AsynchronousCommandBus} will use the monitor
     * configured for the SimpleCommandBus.</p>
     * <p>
     * <p>A component's name matches componentName if they are identical; i.e. they are compared case sensitively.</p>
     *
     * @param componentType         The declared type of the component
     * @param messageMonitorFactory The factory to use
     * @return the current instance of the Configure, for chaining purposes
     */
    Configure configureMessageMonitor(Class<?> componentType, MessageMonitorFactory messageMonitorFactory);

    /**
     * Configures the builder function to create the Message Monitor for the Message processing components in this
     * configuration that match the given class and name.
     * <p>
     * <p>A component matches componentType if componentType is assignable from the component's class. If a component
     * matches multiple types, and the types derive from each other, the configuration from the most derived type is
     * used. If the matching types do not derive from each other, the result is unspecified.</p>
     * <p>
     * <p>For example: in case a monitor is configured for {@link CommandBus} and another monitor is configured for
     * {@link SimpleCommandBus SimpleCommandBus}), components of type
     * {@link AsynchronousCommandBus AsynchronousCommandBus} will use the monitor
     * configured for the SimpleCommandBus.</p>
     * <p>
     * <p>A component's name matches componentName if they are identical; i.e. they are compared case sensitively.</p>
     *
     * @param componentType         The declared type of the component
     * @param componentName         The name of the component
     * @param messageMonitorBuilder The builder function to use
     * @return the current instance of the Configure, for chaining purposes
     */
    default Configure configureMessageMonitor(Class<?> componentType, String componentName,
                                              Function<Configuration, MessageMonitor<Message<?>>> messageMonitorBuilder) {
        return configureMessageMonitor(componentType, componentName,
                                       (configuration, type, name) -> messageMonitorBuilder.apply(configuration));
    }

    /**
     * Configures the factory create the Message Monitor for those Message processing components in this configuration
     * that match the given class and name.
     * <p>
     * <p>A component matches componentType if componentType is assignable from the component's class. If a component
     * matches multiple types, and the types derive from each other, the configuration from the most derived type is
     * used. If the matching types do not derive from each other, the result is unspecified.</p>
     * <p>
     * <p>For example: in case a monitor is configured for {@link CommandBus} and another monitor is configured for
     * {@link SimpleCommandBus SimpleCommandBus}), components of type
     * {@link AsynchronousCommandBus AsynchronousCommandBus} will use the monitor
     * configured for the SimpleCommandBus.</p>
     * <p>
     * <p>A component's name matches componentName if they are identical; i.e. they are compared case sensitively.</p>
     *
     * @param componentType         The declared type of the component
     * @param componentName         The name of the component
     * @param messageMonitorFactory The factory to use
     * @return the current instance of the Configure, for chaining purposes
     */
    Configure configureMessageMonitor(Class<?> componentType, String componentName,
                                      MessageMonitorFactory messageMonitorFactory);

    /**
     * Configures the CorrelationDataProviders that Message processing components should use to attach correlation data
     * to outgoing messages. The builder function receives the Configuration as input and is expected to return a list
     * or CorrelationDataProviders.
     *
     * @param correlationDataProviderBuilder the builder function returning the CorrelationDataProvider list
     * @return the current instance of the Configure, for chaining purposes
     */
    Configure configureCorrelationDataProviders(
            Function<Configuration, List<CorrelationDataProvider>> correlationDataProviderBuilder);

    /**
     * Registers an Axon module with this configuration. The module is initialized when the configuration is created and
     * has access to the global configuration when initialized.
     * <p>
     * Typically, modules are registered for Event Handling components or Sagas.
     *
     * @param module The module to register
     * @return the current instance of the Configure, for chaining purposes
     */
    Configure registerModule(ModuleConfiguration module);

    /**
     * Registers a component which should be made available to other components or modules in this Configuration. The
     * builder function gets this configuration as input, and is expected to provide the component as output.
     * <p>
     * Where possible, it is recommended to use the explicit {@code configure...} and {@code register...} methods.
     *
     * @param componentType    The declared type of the component, typically an interface
     * @param componentBuilder The builder function of this component
     * @param <C>              The type of component
     * @return the current instance of the Configure, for chaining purposes
     */
    <C> Configure registerComponent(Class<C> componentType, Function<Configuration, ? extends C> componentBuilder);

    /**
     * Registers a command handler bean with this configuration. The bean may be of any type. The actual command handler
     * methods will be detected based on the annotations present on the bean's methods.
     * <p>
     * The builder function receives the Configuration as input, and is expected to return a fully initialized instance
     * of the command handler bean.
     *
     * @param annotatedCommandHandlerBuilder The builder function of the Command Handler bean
     * @return the current instance of the Configure, for chaining purposes
     */
    Configure registerCommandHandler(Function<Configuration, Object> annotatedCommandHandlerBuilder);

    /**
     * Registers a query handler bean with this configuration. The bean may be of any type. The actual query handler
     * methods will be detected based on the annotations present on the bean's methods.
     * <p>
     * The builder function receives the Configuration as input, and is expected to return a fully initialized instance
     * of the query handler bean.
     *
     * @param annotatedQueryHandlerBuilder The builder function of the Query Handler bean
     * @return the current instance of the Configure, for chaining purposes
     */
    Configure registerQueryHandler(Function<Configuration, Object> annotatedQueryHandlerBuilder);

    /**
     * Configures the given Command Bus to use in this configuration. The builder receives the Configuration as input
     * and is expected to return a fully initialized {@link CommandBus}
     * instance.
     *
     * @param commandBusBuilder The builder function for the {@link CommandBus}
     * @return the current instance of the Configure, for chaining purposes
     */
    default Configure configureCommandBus(Function<Configuration, CommandBus> commandBusBuilder) {
        return registerComponent(CommandBus.class, commandBusBuilder);
    }

    /**
     * Configures the given Query Bus to use in this configuration. The builder receives the Configuration as input
     * and is expected to return a fully initialized {@link QueryBus}
     * instance.
     *
     * @param queryBusBuilder The builder function for the {@link QueryBus}
     * @return the current instance of the Configure, for chaining purposes
     */
    default Configure configureQueryBus(Function<Configuration, QueryBus> queryBusBuilder) {
        return registerComponent(QueryBus.class, queryBusBuilder);
    }

    /**
     * Configures the given Query Update Emitter to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link QueryUpdateEmitter} instance.
     *
     * @param queryUpdateEmitterBuilder The builder function for the {@link QueryUpdateEmitter}
     * @return the current instance of the Configure, for chaining purposes
     */
    default Configure configureQueryUpdateEmitter(
            Function<Configuration, QueryUpdateEmitter> queryUpdateEmitterBuilder) {
        return registerComponent(QueryUpdateEmitter.class, queryUpdateEmitterBuilder);
    }

    /**
     * Configures the given Serializer to use in this configuration. The builder receives the Configuration as input
     * and is expected to return a fully initialized {@link Serializer}
     * instance.
     *
     * @param serializerBuilder The builder function for the {@link Serializer}
     * @return the current instance of the Configure, for chaining purposes
     */
    default Configure configureSerializer(Function<Configuration, Serializer> serializerBuilder) {
        return registerComponent(Serializer.class, serializerBuilder);
    }

    /**
     * Configures the given event Serializer to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link org.axonframework.serialization.Serializer} instance.
     * <p/>
     * This Serializer is specifically used to serialize EventMessage payload and metadata.
     *
     * @param eventSerializerBuilder The builder function for the {@link org.axonframework.serialization.Serializer}.
     * @return The current instance of the Configure, for chaining purposes.
     */
    Configure configureEventSerializer(Function<Configuration, Serializer> eventSerializerBuilder);

    /**
     * Configures the given event Serializer to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link org.axonframework.serialization.Serializer} instance.
     * <p/>
     * This Serializer is specifically used to serialize Message payload and Metadata.
     *
     * @param messageSerializerBuilder The builder function for the {@link org.axonframework.serialization.Serializer}.
     * @return The current instance of the Configure, for chaining purposes.
     */
    Configure configureMessageSerializer(Function<Configuration, Serializer> messageSerializerBuilder);

    /**
     * Configures the given Transaction Manager to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link TransactionManager}
     * instance.
     *
     * @param transactionManagerBuilder The builder function for the {@link TransactionManager}
     * @return the current instance of the Configure, for chaining purposes
     */
    default Configure configureTransactionManager(
            Function<Configuration, TransactionManager> transactionManagerBuilder) {
        return registerComponent(TransactionManager.class, transactionManagerBuilder);
    }

    /**
     * Returns the completely initialized Configuration built using this configurer. It is not recommended to change
     * any configuration on this Configure once this method is called.
     *
     * @return the fully initialized Configuration
     */
    Configuration buildConfiguration();

    /**
     * Builds the configuration and starts it immediately. It is not recommended to change any configuration on this
     * Configure once this method is called.
     *
     * @return The started configuration
     */
    default Configuration start() {
        Configuration configuration = buildConfiguration();
        configuration.start();
        return configuration;
    }

}
