package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.parameter.ParameterResolver;
import io.iamcyw.tower.messaging.predicate.MessagePredicate;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

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

    public <R> Uni<R> handle(T message) {
        Object result = invoker.invoke(parent.getInstance(), resolveParameter(message));
        if (result instanceof Multi) {
            return (Uni<R>) ((Multi<?>) result).collect().asList();
        } else if (result instanceof Uni) {
            return (Uni<R>) result;
        } else if (result == null) {
            return (Uni<R>) Uni.createFrom().voidItem();
        } else {
            return (Uni<R>) Uni.createFrom().item(result);
        }
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