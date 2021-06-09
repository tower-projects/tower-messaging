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
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.annotation.HandlerDefinition;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.monitoring.MessageMonitor;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryUpdateEmitter;
import io.iamcyw.tower.serialization.Serializer;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Configurer {


    /**
     * Configures the Message Monitor to use for the Message processing components in this configuration, unless more
     * specific configuration based on the component's type, or type and name is available. The builder function
     * receives the type of component as well as its name as input, and is expected to return a MessageMonitor
     * instance to be used by that type of component.
     *
     * @param messageMonitorFactoryBuilder The MessageMonitor builder function
     * @return the current instance of the Configurer, for chaining purposes
     */
    Configurer configureMessageMonitor(Function<Configuration, BiFunction<Class<?>, String, MessageMonitor<Message<?>>>> messageMonitorFactoryBuilder);

    default Configurer configureMessageMonitor(Class<?> componentType, Function<Configuration, MessageMonitor<Message<?>>> messageMonitorBuilder) {
        return configureMessageMonitor(componentType,
                                       (configuration, type, name) -> messageMonitorBuilder.apply(configuration));
    }

    Configurer configureMessageMonitor(Class<?> componentType, MessageMonitorFactory messageMonitorFactory);

    default Configurer configureMessageMonitor(Class<?> componentType, String componentName, Function<Configuration, MessageMonitor<Message<?>>> messageMonitorBuilder) {
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
     * {@link io.iamcyw.tower.commandhandling.SimpleCommandBus SimpleCommandBus}), components of type
     * {@link io.iamcyw.tower.commandhandling.AsynchronousCommandBus AsynchronousCommandBus} will use the monitor
     * configured for the SimpleCommandBus.</p>
     * <p>
     * <p>A component's name matches componentName if they are identical; i.e. they are compared case sensitively.</p>
     *
     * @param componentType         The declared type of the component
     * @param componentName         The name of the component
     * @param messageMonitorFactory The factory to use
     * @return the current instance of the Configurer, for chaining purposes
     */
    Configurer configureMessageMonitor(Class<?> componentType, String componentName, MessageMonitorFactory messageMonitorFactory);

    /**
     * Configures the CorrelationDataProviders that Message processing components should use to attach correlation data
     * to outgoing messages. The builder function receives the Configuration as input and is expected to return a list
     * or CorrelationDataProviders.
     *
     * @param correlationDataProviderBuilder the builder function returning the CorrelationDataProvider list
     * @return the current instance of the Configurer, for chaining purposes
     */
    Configurer configureCorrelationDataProviders(Function<Configuration, List<CorrelationDataProvider>> correlationDataProviderBuilder);


    /**
     * Registers a component which should be made available to other components or modules in this Configuration. The
     * builder function gets this configuration as input, and is expected to provide the component as output.
     * <p>
     * Where possible, it is recommended to use the explicit {@code configure...} and {@code register...} methods.
     *
     * @param componentType    The declared type of the component, typically an interface
     * @param componentBuilder The builder function of this component
     * @param <C>              The type of component
     * @return the current instance of the Configurer, for chaining purposes
     */
    <C> Configurer registerComponent(Class<C> componentType, Function<Configuration, ? extends C> componentBuilder);

    /**
     * Registers a command handler bean with this {@link Configurer}. The bean may be of any type. The actual command
     * handler methods will be detected based on the annotations present on the bean's methods. Message handling
     * functions annotated with {@link io.iamcyw.tower.commandhandling.CommandHandler} will be taken into account.
     * <p>
     * The builder function receives the {@link Configuration} as input, and is expected to return a fully initialized
     * instance of the command handler bean.
     *
     * @param commandHandlerBuilder the builder function of the command handler bean
     * @return the current instance of the {@link Configurer}, for chaining purposes
     */
    Configurer registerCommandHandler(Function<Configuration, Object> commandHandlerBuilder);

    /**
     * Registers a command handler bean with this {@link Configurer}. The bean may be of any type. The actual command
     * handler methods will be detected based on the annotations present on the bean's methods. Message handling
     * functions annotated with {@link io.iamcyw.tower.commandhandling.CommandHandler} will be taken into account.
     * <p>
     * The builder function receives the {@link Configuration} as input, and is expected to return a fully initialized
     * instance of the command handler bean.
     *
     * @param commandHandlerBuilder the builder function of the command handler bean
     * @param phase                 defines a phase in which the command handler builder will be invoked during {@link
     *                              Configuration#start()} and {@link Configuration#shutdown()}. When starting the
     *                              configuration handlers are ordered in ascending, when shutting down the
     *                              configuration, descending order is used.
     * @return the current instance of the {@link Configurer}, for chaining purposes
     * @deprecated in favor of {@link #registerCommandHandler(Function)}, since the {@code phase} of an annotated
     * handler should be defined through the {@link io.iamcyw.tower.lifecycle.StartHandler}/{@link
     * io.iamcyw.tower.lifecycle.ShutdownHandler} annotation.
     */
    @Deprecated
    default Configurer registerCommandHandler(int phase, Function<Configuration, Object> commandHandlerBuilder) {
        return registerCommandHandler(commandHandlerBuilder);
    }

    /**
     * Registers a query handler bean with this {@link Configurer}. The bean may be of any type. The actual query
     * handler methods will be detected based on the annotations present on the bean's methods. Message handling
     * functions annotated with {@link io.iamcyw.tower.queryhandling.QueryHandler} will be taken into account.
     * <p>
     * The builder function receives the {@link Configuration} as input, and is expected to return a fully initialized
     * instance of the query handler bean.
     *
     * @param queryHandlerBuilder the builder function of the query handler bean
     * @return the current instance of the {@link Configurer}, for chaining purposes
     */
    Configurer registerQueryHandler(Function<Configuration, Object> queryHandlerBuilder);

    /**
     * Registers a query handler bean with this {@link Configurer}. The bean may be of any type. The actual query
     * handler methods will be detected based on the annotations present on the bean's methods. Message handling
     * functions annotated with {@link io.iamcyw.tower.queryhandling.QueryHandler} will be taken into account.
     * <p>
     * The builder function receives the {@link Configuration} as input, and is expected to return a fully initialized
     * instance of the query handler bean.
     *
     * @param queryHandlerBuilder the builder function of the query handler bean
     * @param phase               defines a phase in which the query handler builder will be invoked during {@link
     *                            Configuration#start()} and {@link Configuration#shutdown()}. When starting the
     *                            configuration handlers are ordered in ascending, when shutting down the configuration,
     *                            descending order is used.
     * @return the current instance of the {@link Configurer}, for chaining purposes
     * @deprecated in favor of {@link #registerQueryHandler(Function)}, since the {@code phase} of an annotated handler
     * should be defined through the {@link io.iamcyw.tower.lifecycle.StartHandler}/{@link
     * io.iamcyw.tower.lifecycle.ShutdownHandler} annotation.
     */
    @Deprecated
    default Configurer registerQueryHandler(int phase, Function<Configuration, Object> queryHandlerBuilder) {
        return registerQueryHandler(queryHandlerBuilder);
    }

    /**
     * Registers a message handler bean with this configuration. The bean may be of any type. The actual message handler
     * methods will be detected based on the annotations present on the bean's methods. Message handling functions
     * annotated with {@link io.iamcyw.tower.commandhandling.CommandHandler},{@link io.iamcyw.tower.queryhandling.QueryHandler} will be
     * taken into account.
     * <p>
     * The builder function receives the {@link Configuration} as input, and is expected to return a fully initialized
     * instance of the message handler bean.
     *
     * @param messageHandlerBuilder the builder function of the message handler bean
     * @return the current instance of the {@link Configurer}, for chaining purposes
     */
    Configurer registerMessageHandler(Function<Configuration, Object> messageHandlerBuilder);


    /**
     * Configures the given Command Bus to use in this configuration. The builder receives the Configuration as input
     * and is expected to return a fully initialized {@link CommandBus}
     * instance.
     *
     * @param commandBusBuilder The builder function for the {@link CommandBus}
     * @return the current instance of the Configurer, for chaining purposes
     */
    default Configurer configureCommandBus(Function<Configuration, CommandBus> commandBusBuilder) {
        return registerComponent(CommandBus.class, commandBusBuilder);
    }

    /**
     * Configures the given Query Bus to use in this configuration. The builder receives the Configuration as input
     * and is expected to return a fully initialized {@link QueryBus}
     * instance.
     *
     * @param queryBusBuilder The builder function for the {@link QueryBus}
     * @return the current instance of the Configurer, for chaining purposes
     */
    default Configurer configureQueryBus(Function<Configuration, QueryBus> queryBusBuilder) {
        return registerComponent(QueryBus.class, queryBusBuilder);
    }

    /**
     * Configures the given Query Update Emitter to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link QueryUpdateEmitter} instance.
     *
     * @param queryUpdateEmitterBuilder The builder function for the {@link QueryUpdateEmitter}
     * @return the current instance of the Configurer, for chaining purposes
     */
    default Configurer configureQueryUpdateEmitter(Function<Configuration, QueryUpdateEmitter> queryUpdateEmitterBuilder) {
        return registerComponent(QueryUpdateEmitter.class, queryUpdateEmitterBuilder);
    }

    /**
     * Configures the given Serializer to use in this configuration. The builder receives the Configuration as input
     * and is expected to return a fully initialized {@link Serializer}
     * instance.
     *
     * @param serializerBuilder The builder function for the {@link Serializer}
     * @return the current instance of the Configurer, for chaining purposes
     */
    default Configurer configureSerializer(Function<Configuration, Serializer> serializerBuilder) {
        return registerComponent(Serializer.class, serializerBuilder);
    }

    /**
     * Configures the given event Serializer to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link io.iamcyw.tower.serialization.Serializer} instance.
     * <p/>
     * This Serializer is specifically used to serialize EventMessage payload and metadata.
     *
     * @param eventSerializerBuilder The builder function for the {@link io.iamcyw.tower.serialization.Serializer}.
     * @return The current instance of the Configurer, for chaining purposes.
     */
    Configurer configureEventSerializer(Function<Configuration, Serializer> eventSerializerBuilder);

    /**
     * Configures the given event Serializer to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link io.iamcyw.tower.serialization.Serializer} instance.
     * <p/>
     * This Serializer is specifically used to serialize Message payload and Metadata.
     *
     * @param messageSerializerBuilder The builder function for the {@link io.iamcyw.tower.serialization.Serializer}.
     * @return The current instance of the Configurer, for chaining purposes.
     */
    Configurer configureMessageSerializer(Function<Configuration, Serializer> messageSerializerBuilder);

    /**
     * Configures the given Transaction Manager to use in this configuration. The builder receives the Configuration as
     * input and is expected to return a fully initialized {@link TransactionManager}
     * instance.
     *
     * @param transactionManagerBuilder The builder function for the {@link TransactionManager}
     * @return the current instance of the Configurer, for chaining purposes
     */
    default Configurer configureTransactionManager(Function<Configuration, TransactionManager> transactionManagerBuilder) {
        return registerComponent(TransactionManager.class, transactionManagerBuilder);
    }


    /**
     * Registers the definition of a Handler class. Defaults to annotation based recognition of handler methods.
     *
     * @param handlerDefinitionClass A function providing the definition based on the current Configuration as well
     *                               as the class being inspected.
     * @return the current instance of the Configurer, for chaining purposes
     */
    Configurer registerHandlerDefinition(BiFunction<Configuration, Class, HandlerDefinition> handlerDefinitionClass);


    /**
     * Returns the completely initialized Configuration built using this configurer. It is not recommended to change
     * any configuration on this Configurer once this method is called.
     *
     * @return the fully initialized Configuration
     */
    Configuration buildConfiguration();

    /**
     * Builds the configuration and starts it immediately. It is not recommended to change any configuration on this
     * Configurer once this method is called.
     *
     * @return The started configuration
     */
    default Configuration start() {
        Configuration configuration = buildConfiguration();
        configuration.start();
        return configuration;
    }

}
