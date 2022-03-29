package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MessageBean;
import io.iamcyw.tower.messaging.handle.resolve.ParameterResolver;
import io.iamcyw.tower.messaging.handle.resolve.ParameterResolverWrapper;
import io.iamcyw.tower.messaging.predicate.MessageHandlePredicate;
import io.iamcyw.tower.responsetype.ResponseType;
import io.iamcyw.tower.utils.Assert;

import java.util.function.Supplier;

public class DefaultMessageHandle extends MessageHandle {
    private MessageHandlePredicate messageHandlePredicate;

    private Supplier<MethodInvoker> invoker;

    private MessageBean messageBean;

    private ParameterResolverWrapper resolvers;

    private Class<?> returnType;

    public DefaultMessageHandle() {
        this("", message -> true, () -> null, new MessageBean(), new ParameterResolver[]{}, Object.class);
    }

    public DefaultMessageHandle(String handleTarget, MessageHandlePredicate messageHandlePredicate,
                                Supplier<MethodInvoker> invoker, MessageBean messageBean,
                                ParameterResolver<?>[] resolvers, Class<?> returnType) {
        super(handleTarget);
        this.messageHandlePredicate = messageHandlePredicate;
        this.invoker = invoker;
        this.messageBean = messageBean;
        this.resolvers = new ParameterResolverWrapper(resolvers);
        this.returnType = returnType;
    }

    public DefaultMessageHandle(String handleTarget, MessageHandlePredicate messageHandlePredicate,
                                Supplier<MethodInvoker> invoker, MessageBean messageBean,
                                ParameterResolverWrapper resolvers) {
        super(handleTarget);
        this.messageHandlePredicate = messageHandlePredicate;
        this.invoker = invoker;
        this.messageBean = messageBean;
        this.resolvers = resolvers;
    }


    @Override
    public <R> R handle(Message message) {
        return invoker.get()
                      .invoke(messageBean.getBeanFactory().createInstance().getInstance(),
                              resolvers.resolveParameter(message));
    }

    @Override
    public boolean predicate(Message message) {
        return getMessageHandlePredicate().test(message) && checkReturnType(message);
    }

    public boolean checkReturnType(Message message) {
        ResponseType<?> responseType = message.getMetaData().getResponseType();
        Assert.assertNotNull(responseType);

        return responseType.matches(returnType);
    }

    public MessageHandlePredicate getMessageHandlePredicate() {
        return messageHandlePredicate;
    }

    public void setMessageHandlePredicate(MessageHandlePredicate messageHandlePredicate) {
        this.messageHandlePredicate = messageHandlePredicate;
    }

    public Supplier<MethodInvoker> getInvoker() {
        return invoker;
    }

    public void setInvoker(Supplier<MethodInvoker> invoker) {
        this.invoker = invoker;
    }

    public MessageBean getMessageBean() {
        return messageBean;
    }

    public void setMessageBean(MessageBean messageBean) {
        this.messageBean = messageBean;
    }

    public ParameterResolverWrapper getResolvers() {
        return resolvers;
    }

    public void setResolvers(ParameterResolverWrapper resolvers) {
        this.resolvers = resolvers;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

}
