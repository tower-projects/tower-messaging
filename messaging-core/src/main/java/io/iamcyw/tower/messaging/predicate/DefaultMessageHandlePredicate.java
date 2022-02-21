package io.iamcyw.tower.messaging.predicate;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MessageBean;
import io.iamcyw.tower.messaging.handle.MethodInvoker;
import io.iamcyw.tower.messaging.handle.resolve.ParameterResolver;
import io.iamcyw.tower.messaging.handle.resolve.ParameterResolverWrapper;

import java.util.function.Supplier;

public class DefaultMessageHandlePredicate implements MessageHandlePredicate {
    private final String name;

    private final Supplier<MethodInvoker> invoker;

    private final MessageBean messageBean;

    private final ParameterResolverWrapper resolvers;

    public DefaultMessageHandlePredicate(String name, Supplier<MethodInvoker> invoker, MessageBean messageBean,
                                         ParameterResolver<?>[] resolvers) {
        this.name = name;
        this.invoker = invoker;
        this.messageBean = messageBean;
        this.resolvers = new ParameterResolverWrapper(resolvers);
    }

    @Override
    public boolean test(Message message) {
        return invoker.get()
                      .invoke(messageBean.getBeanFactory().createInstance().getInstance(),
                              resolvers.resolveParameter(message));
    }

}
