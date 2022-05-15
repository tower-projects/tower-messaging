package io.iamcyw.tower.messaging.handle.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DefaultInterceptorChain implements InterceptorChain {

    private final MessageInterceptor interceptor;

    private final InterceptorChain nextChain;

    public DefaultInterceptorChain(MessageInterceptor interceptor, InterceptorChain nextChain) {
        this.interceptor = interceptor;
        this.nextChain = nextChain;
    }

    public static InterceptorChain build(Deque<MessageInterceptor> interceptors, InterceptorChain nextChain) {
        if (interceptors.isEmpty()) {
            return nextChain;
        } else {
            return build(interceptors, new DefaultInterceptorChain(interceptors.removeLast(), nextChain));
        }
    }

    public static CompletableFuture<InterceptorChain> buildChain(List<MessageInterceptor> interceptors,
                                                                 Supplier<InterceptorChain> handle) {
        return CompletableFuture.completedFuture(build(new ArrayDeque<>(interceptors), handle.get()));
    }

    @Override
    public <R> CompletableFuture<R> filter(Message<R> message) {
        return interceptor.filter(message, nextChain);
    }

}
