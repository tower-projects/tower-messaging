package io.iamcyw.tower.commandhandling.model.inspection;

import io.iamcyw.tower.commandhandling.CommandMessageHandlingMember;
import io.iamcyw.tower.messaging.annotation.AnnotatedHandlerInspector;
import io.iamcyw.tower.messaging.annotation.MessageHandlingMember;
import io.iamcyw.tower.messaging.annotation.ParameterResolverFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotatedAggregateMetaModelFactory implements AggregateMetaModelFactory {

    private final Map<Class<?>, AnnotatedAggregateModel> registry;

    private final ParameterResolverFactory parameterResolverFactory;

    public AnnotatedAggregateMetaModelFactory() {
        this(null);
    }

    public AnnotatedAggregateMetaModelFactory(ParameterResolverFactory parameterResolverFactory) {
        this.parameterResolverFactory = parameterResolverFactory;
        registry = new ConcurrentHashMap<>();
    }

    public static <T> AggregateModel<T> inspectAggregate(Class<T> aggregateType,
                                                         ParameterResolverFactory parameterResolverFactory) {
        return new AnnotatedAggregateMetaModelFactory(parameterResolverFactory).createModel(aggregateType);
    }

    @Override
    public <T> AggregateModel<T> createModel(Class<T> aggregateType) {
        if (!registry.containsKey(aggregateType)) {
            AnnotatedHandlerInspector<T> inspector;
            if (parameterResolverFactory == null) {
                inspector = AnnotatedHandlerInspector.inspectType(aggregateType);
            } else {
                inspector = AnnotatedHandlerInspector.inspectType(aggregateType, parameterResolverFactory);
            }
            AnnotatedAggregateModel<T> model = new AnnotatedAggregateModel<>(aggregateType, inspector);
            // Add the newly created inspector to the registry first to prevent a StackOverflowError:
            // another call to createInspector with the same inspectedType will return this instance of the inspector.
            registry.put(aggregateType, model);
            model.initialize();
        }
        //noinspection unchecked
        return registry.get(aggregateType);
    }

    private class AnnotatedAggregateModel<T> implements AggregateModel<T> {

        private final Class<? extends T> inspectedType;

        private final AnnotatedHandlerInspector<T> handlerInspector;

        private final List<MessageHandlingMember<? super T>> commandHandlerInterceptors;

        private final Map<String, MessageHandlingMember<? super T>> commandHandlers;

        private final List<MessageHandlingMember<? super T>> eventHandlers;

        private String aggregateType;

        private String routingKey;

        public AnnotatedAggregateModel(Class<? extends T> aggregateType,
                                       AnnotatedHandlerInspector<T> handlerInspector) {
            this.inspectedType = aggregateType;
            this.commandHandlerInterceptors = new ArrayList<>();
            this.commandHandlers = new HashMap<>();
            this.eventHandlers = new ArrayList<>();
            this.handlerInspector = handlerInspector;
        }

        private void initialize() {
            inspectAggregateType();
            prepareHandlers();
        }

        @SuppressWarnings("unchecked")
        private void prepareHandlers() {
            for (MessageHandlingMember<? super T> handler : handlerInspector.getHandlers()) {
                Optional<CommandMessageHandlingMember> commandHandler = handler
                        .unwrap(CommandMessageHandlingMember.class);
                Optional<CommandHandlerInterceptorHandlingMember> unwrappedCommandHandlerInterceptor = handler
                        .unwrap(CommandHandlerInterceptorHandlingMember.class);
                if (commandHandler.isPresent()) {
                    commandHandlers.putIfAbsent(commandHandler.get().commandName(), handler);
                } else if (unwrappedCommandHandlerInterceptor.isPresent()) {
                    commandHandlerInterceptors.add(handler);
                } else {
                    eventHandlers.add(handler);
                }
            }
        }

        private void inspectAggregateType() {
            aggregateType = inspectedType.getSimpleName();
        }

        @Override
        public String type() {
            return aggregateType;
        }

        @Override
        public String routingKey() {
            return routingKey;
        }

        @Override
        public Map<String, MessageHandlingMember<? super T>> commandHandlers() {
            return Collections.unmodifiableMap(commandHandlers);
        }

        @Override
        public List<MessageHandlingMember<? super T>> commandHandlerInterceptors() {
            return Collections.unmodifiableList(commandHandlerInterceptors);
        }

        @Override
        public Class<? extends T> entityClass() {
            return inspectedType;
        }

    }

}
