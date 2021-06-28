/*
 * Copyright (c) 2010-2018. Axon Framework
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

package io.iamcyw.tower.commandhandling;


import io.iamcyw.tower.commandhandling.model.inspection.AggregateModel;
import io.iamcyw.tower.commandhandling.model.inspection.AnnotatedAggregateMetaModelFactory;
import io.iamcyw.tower.common.Registration;
import io.iamcyw.tower.messaging.MessageHandler;
import io.iamcyw.tower.messaging.annotation.ClasspathParameterResolverFactory;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;
import io.iamcyw.tower.utils.Assert;
import io.iamcyw.tower.utils.i18n.I18ns;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationCommandHandlerAdapter implements MessageHandler<CommandMessage<?>>, SupportedCommandNamesAware {

    private final Object target;

    private final AggregateModel<Object> modelInspector;

    /**
     * Wraps the given {@code annotatedCommandHandler}, allowing it to be subscribed to a Command Bus.
     *
     * @param annotatedCommandHandler The object containing the @CommandHandler annotated methods
     */
    public AnnotationCommandHandlerAdapter(Object annotatedCommandHandler) {
        this(annotatedCommandHandler, ClasspathParameterResolverFactory.forClass(annotatedCommandHandler.getClass()));
    }

    /**
     * Wraps the given {@code annotatedCommandHandler}, allowing it to be subscribed to a Command Bus.
     *
     * @param annotatedCommandHandler  The object containing the @CommandHandler annotated methods
     * @param parameterResolverFactory The strategy for resolving handler method parameter values
     */
    @SuppressWarnings("unchecked")
    public AnnotationCommandHandlerAdapter(Object annotatedCommandHandler,
                                           ParameterResolverFactory parameterResolverFactory) {
        Assert.nonNull(annotatedCommandHandler,
                       I18ns.create().content("annotatedCommandHandler may not be null").apply());
        this.modelInspector = AnnotatedAggregateMetaModelFactory
                .inspectAggregate((Class<Object>) annotatedCommandHandler.getClass(), parameterResolverFactory);

        this.target = annotatedCommandHandler;
    }

    /**
     * Subscribe this command handler to the given {@code commandBus}. The command handler will be subscribed
     * for each of the supported commands.
     *
     * @param commandBus The command bus instance to subscribe to
     * @return A handle that can be used to unsubscribe
     */
    public Registration subscribe(CommandBus commandBus) {
        Collection<Registration> subscriptions = supportedCommandNames().stream().map(supportedCommand -> commandBus
                .subscribe(supportedCommand, this)).collect(Collectors.toCollection(ArrayDeque::new));
        return () -> subscriptions.stream().map(Registration::cancel).reduce(Boolean::logicalOr).orElse(false);
    }

    /**
     * Invokes the @CommandHandler annotated method that accepts the given {@code command}.
     *
     * @param command The command to handle
     * @return the result of the command handling. Is {@code null} when the annotated handler has a
     * {@code void} return value.
     * @throws NoHandlerForCommandException when no handler is found for given {@code command}.
     * @throws Exception                    any exception occurring while handling the command
     */
    @Override
    public Object handle(CommandMessage<?> command) throws Exception {
        return modelInspector.commandHandler(command.getCommandName()).handle(command, target);
    }


    @Override
    public Set<String> supportedCommandNames() {
        return modelInspector.commandHandlers().keySet();
    }

}
