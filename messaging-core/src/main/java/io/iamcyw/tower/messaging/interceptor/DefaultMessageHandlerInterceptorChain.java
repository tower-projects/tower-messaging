package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultMessageHandlerInterceptorChain<R> implements MessageHandlerInterceptorChain<R> {

    private final MessageHandlerInterceptor<R> interceptor;

    private final MessageHandlerInterceptorChain<R> nextChain;

    public DefaultMessageHandlerInterceptorChain(MessageHandlerInterceptor<R> interceptor,
                                                 MessageHandlerInterceptorChain<R> nextChain) {
        this.interceptor = interceptor;
        this.nextChain = nextChain;
    }

    public static <R1> MessageHandlerInterceptorChain<R1> build(Deque<MessageHandlerInterceptor<R1>> interceptors,
                                                                MessageHandlerInterceptorChain<R1> nextChain) {
        if (interceptors.isEmpty()) {
            return nextChain;
        } else {
            return build(interceptors,
                         new DefaultMessageHandlerInterceptorChain<R1>(interceptors.removeLast(), nextChain));
        }
    }

    public static <R1> CompletableFuture<MessageHandlerInterceptorChain<R1>> buildChain(
            List<MessageHandlerInterceptor<R1>> interceptors, Handle<R1> handle) {
        return CompletableFuture.completedFuture(build(new ArrayDeque<>(interceptors), handle::handle));
    }

    @Override
    public CompletableFuture<R> filter(Message message) {
        return interceptor.filter(message, nextChain);
    }

}
