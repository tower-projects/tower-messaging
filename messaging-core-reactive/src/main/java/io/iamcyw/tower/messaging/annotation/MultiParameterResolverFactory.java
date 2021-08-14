package io.iamcyw.tower.messaging.annotation;


import io.iamcyw.tower.common.Priority;
import io.iamcyw.tower.common.annotation.PriorityAnnotationComparator;
import io.iamcyw.tower.messaging.parameter.ParameterResolver;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ParameterResolverFactory instance that delegates to multiple other instances, in the order provided.
 */
public class MultiParameterResolverFactory implements ParameterResolverFactory {

    private final ParameterResolverFactory[] factories;

    /**
     * Initializes an instance that delegates to the given {@code delegates}, in the order provided. Changes in
     * the given array are not reflected in the created instance.
     *
     * @param delegates The factories providing the parameter values to use
     */
    public MultiParameterResolverFactory(ParameterResolverFactory... delegates) {
        this.factories = Arrays.copyOf(delegates, delegates.length);
    }

    /**
     * Initializes an instance that delegates to the given {@code delegates}, in the order provided. Changes in
     * the given List are not reflected in the created instance.
     *
     * @param delegates The list of factories providing the parameter values to use
     */
    public MultiParameterResolverFactory(List<ParameterResolverFactory> delegates) {
        this.factories = delegates.toArray(new ParameterResolverFactory[0]);
    }

    /**
     * Creates a MultiParameterResolverFactory instance with the given {@code delegates}, which are automatically
     * ordered based on the {@link Priority @Priority} annotation on their respective classes.
     * Classes with the same Priority are kept in the order as provided in the {@code delegates}.
     * <p>
     * If one of the delegates is a MultiParameterResolverFactory itself, that factory's delegates are 'mixed' with
     * the given {@code delegates}, based on their respective order.
     *
     * @param delegates The delegates to include in the factory
     * @return an instance delegating to the given {@code delegates}
     */
    public static MultiParameterResolverFactory ordered(ParameterResolverFactory... delegates) {
        return ordered(Arrays.asList(delegates));
    }

    /**
     * Creates a MultiParameterResolverFactory instance with the given {@code delegates}, which are automatically
     * ordered based on the {@link Priority @Priority} annotation on their respective classes.
     * Classes with the same Priority are kept in the order as provided in the {@code delegates}.
     * <p>
     * If one of the delegates is a MultiParameterResolverFactory itself, that factory's delegates are 'mixed' with
     * the given {@code delegates}, based on their respective order.
     *
     * @param delegates The delegates to include in the factory
     * @return an instance delegating to the given {@code delegates}
     */
    public static MultiParameterResolverFactory ordered(List<ParameterResolverFactory> delegates) {
        return new MultiParameterResolverFactory(flatten(delegates));
    }

    private static ParameterResolverFactory[] flatten(List<ParameterResolverFactory> factories) {
        List<ParameterResolverFactory> flattened = new ArrayList<>(factories.size());
        for (ParameterResolverFactory parameterResolverFactory : factories) {
            if (parameterResolverFactory instanceof MultiParameterResolverFactory) {
                flattened.addAll(((MultiParameterResolverFactory) parameterResolverFactory).getDelegates());
            } else {
                flattened.add(parameterResolverFactory);
            }
        }
        flattened.sort(PriorityAnnotationComparator.getInstance());
        return flattened.toArray(new ParameterResolverFactory[0]);
    }

    /**
     * Returns the delegates of this instance, in the order they are evaluated to resolve parameters.
     *
     * @return the delegates of this instance, in the order they are evaluated to resolve parameters
     */
    public List<ParameterResolverFactory> getDelegates() {
        return Arrays.asList(factories);
    }


    @Override
    public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {
        for (ParameterResolverFactory factory : factories) {
            ParameterResolver resolver = factory.createInstance(executable, parameters, parameterIndex);
            if (resolver != null) {
                return resolver;
            }
        }
        return null;
    }

}
