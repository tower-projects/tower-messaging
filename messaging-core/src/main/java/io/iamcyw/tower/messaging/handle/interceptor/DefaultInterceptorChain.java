package io.iamcyw.tower.messaging.handle.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DefaultInterceptorChain<R> implements InterceptorChain {

    private final MessageInterceptor<R> interceptor;

    private final InterceptorChain<R> nextChain;

    public DefaultInterceptorChain(MessageInterceptor<R> interceptor, InterceptorChain<R> nextChain) {
        this.interceptor = interceptor;
        this.nextChain = nextChain;
    }

    public static <R> InterceptorChain<R> build(Deque<MessageInterceptor<R>> interceptors,
                                                InterceptorChain<R> nextChain) {
        if (interceptors.isEmpty()) {
            return nextChain;
        } else {
            return build(interceptors, new DefaultInterceptorChain<R>(interceptors.removeLast(), nextChain));
        }
    }

    public static <R> CompletableFuture<InterceptorChain<R>> buildChain(List<MessageInterceptor<R>> interceptors,
                                                                        Supplier<InterceptorChain<R>> handle) {
        return CompletableFuture.completedFuture(build(new ArrayDeque<>(interceptors), handle.get()));
    }

    @Override
    public CompletableFuture filter(Message message) {
        return interceptor.filter(message, nextChain);
    }

}
