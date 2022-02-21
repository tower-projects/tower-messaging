package io.iamcyw.tower.messaging.interceptor;

import io.iamcyw.tower.messaging.Message;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class DefaultMessageHandlerInterceptorChain implements MessageHandlerInterceptorChain {

    private final MessageHandlerInterceptor interceptor;

    private final MessageHandlerInterceptorChain nextChain;

    public DefaultMessageHandlerInterceptorChain(MessageHandlerInterceptor interceptor,
                                                 MessageHandlerInterceptorChain nextChain) {
        this.interceptor = interceptor;
        this.nextChain = nextChain;
    }

    public static MessageHandlerInterceptorChain build(Deque<MessageHandlerInterceptor> interceptors,
                                                       MessageHandlerInterceptorChain nextChain) {
        if (interceptors.isEmpty()) {
            return nextChain;
        } else {
            return build(interceptors, new DefaultMessageHandlerInterceptorChain(interceptors.removeLast(), nextChain));
        }
    }

    public static <R1> MessageHandlerInterceptorChain buildChain(List<MessageHandlerInterceptor> interceptors,
                                                                 Handle<R1> handle) {
        return build(new ArrayDeque<>(interceptors), new MessageHandlerInterceptorChain() {
            @Override
            public <R> R filter(Message message) {
                return (R) handle.handle(message);
            }
        });
    }

    @Override
    public <R> R filter(Message message) {
        return interceptor.filter(message, nextChain);
    }

}
