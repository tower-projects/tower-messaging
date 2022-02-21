package io.iamcyw.tower.messaging.handle.resolve;

import io.iamcyw.tower.messaging.Message;

import java.util.Arrays;

public class ParameterResolverWrapper {
    private ParameterResolver<?>[] resolvers;

    public ParameterResolverWrapper(ParameterResolver<?>[] resolvers) {
        this.resolvers = resolvers;
    }

    public ParameterResolverWrapper() {
        this(null);
    }

    public Object[] resolveParameter(Message message) {
        Object[] args = new Object[resolvers.length];
        Arrays.setAll(args, index -> {
            ParameterResolver<?> resolver = resolvers[index];
            if (resolver.matches(message)) {
                return resolver.resolveParameterValue(message);
            } else {
                return null;
            }
        });
        return args;
    }

    public ParameterResolver<?>[] getResolvers() {
        return resolvers;
    }

    public void setResolvers(ParameterResolver<?>[] resolvers) {
        this.resolvers = resolvers;
    }

}
