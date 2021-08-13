package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.annotation.ParameterResolver;
import io.iamcyw.tower.messaging.predicate.MessagePredicate;
import io.smallrye.mutiny.Multi;

public abstract class ReactorMessageMethod<T extends Message> {

    protected EndpointInvoker invoker;

    protected MessageClass parent;

    protected MessagePredicate<T> predicate;

    protected ParameterResolver<?>[] resolvers;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean canHandle(T message) {
        return predicate.test(message);
    }

    public EndpointInvoker getInvoker() {
        return invoker;
    }

    public void setInvoker(EndpointInvoker invoker) {
        this.invoker = invoker;
    }

    public <R> Multi<R> handle(T message) {
        return invoker.invoke(parent.getInstance(), resolveParameter(message));
    }

    public Object[] resolveParameter(T message) {
        Object[] args = new Object[resolvers.length];
        for (int i = 0; i < resolvers.length; i++) {
            ParameterResolver<?> resolver = resolvers[i];
            if (resolver.matches(message)) {
                args[i] = resolver.resolveParameterValue(message);
            }
        }
        return args;
    }

    public void setPredicate(MessagePredicate<T> predicate) {
        this.predicate = predicate;
    }

    public void setResolvers(ParameterResolver<?>[] resolvers) {
        this.resolvers = resolvers;
    }

    public void setParent(MessageClass parent) {
        this.parent = parent;
    }

}