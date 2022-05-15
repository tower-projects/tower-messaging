package io.iamcyw.tower.messaging.cdi;

import io.iamcyw.tower.Assert;
import io.iamcyw.tower.messaging.DefaultMessageBus;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.bootstrap.Bootstrap;
import io.iamcyw.tower.messaging.handle.MessageHandle;
import io.iamcyw.tower.messaging.handle.interceptor.DefaultInterceptorChain;
import io.iamcyw.tower.messaging.handle.interceptor.InterceptorChain;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

import java.util.concurrent.CompletableFuture;

public class CDIMessageBus extends DefaultMessageBus {
    ThreadContext threadContext;

    ManagedExecutor managedExecutor;

    public CDIMessageBus(Bootstrap bootstrap, ThreadContext threadContext, ManagedExecutor managedExecutor) {
        super(bootstrap);
        this.managedExecutor = managedExecutor;
        this.threadContext = threadContext;
    }

    @Override
    public <R> CompletableFuture<R> handle(Message<R> message) {

        return DefaultInterceptorChain.buildChain(bootstrap.getMessageInterceptors(), () -> new InterceptorChain() {
            @Override
            public <R> CompletableFuture<R> filter(Message<R> msg) {
                CompletableFuture<MessageHandle<R>> messageHandleCF = msg.getMetaData().getMessageHandle();
                Assert.assertNotNull(messageHandleCF, "MessageHandle");

                return threadContext.withContextCapture(
                        messageHandleCF.thenComposeAsync(handle -> handle.handle(msg), managedExecutor));
            }
        }).thenCompose(chain -> chain.filter(message));
    }

}
