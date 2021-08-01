package io.iamcyw.tower.messaging.predicate;

import io.iamcyw.tower.messaging.HandlerMethod;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.utils.Assert;

import java.util.function.BiPredicate;

public interface MessagePredicate<T extends Message<?>> extends BiPredicate<T, HandlerMethod> {

    static <T> MessagePredicate<? super T> wrapIfNeeded(BiPredicate<? super T, ? super HandlerMethod> other) {
        MessagePredicate<? super T> right;

        if (other instanceof MessagePredicate) {
            right = (MessagePredicate<? super T>) other;
        } else {
            right = new MessagePredicateWrapper(other);
        }
        return right;
    }

    @Override
    default BiPredicate<T, HandlerMethod> and(BiPredicate<? super T, ? super HandlerMethod> other) {
        return new AndMessagePredicate<>(this, wrapIfNeeded(other));
    }

    @Override
    default BiPredicate<T, HandlerMethod> negate() {
        return new NegateMessagePredicate<>(this);
    }

    @Override
    default BiPredicate<T, HandlerMethod> or(BiPredicate<? super T, ? super HandlerMethod> other) {
        return new OrMessagePredicate<>(this, wrapIfNeeded(other));
    }

    class MessagePredicateWrapper<A extends Message<?>> implements MessagePredicate<A> {

        private final BiPredicate<A, HandlerMethod> delegate;

        public MessagePredicateWrapper(BiPredicate<A, HandlerMethod> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean test(A a, HandlerMethod handlerMethod) {
            return delegate.test(a, handlerMethod);
        }

        @Override
        public String toString() {
            return this.delegate.getClass().getSimpleName();
        }

    }

    class NegateMessagePredicate<A extends Message<?>> implements MessagePredicate<A> {

        private final MessagePredicate<A> predicate;

        public NegateMessagePredicate(MessagePredicate<A> predicate) {
            Assert.nonNull(predicate, "predicate GatewayPredicate must not be null");
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, HandlerMethod handlerMethod) {
            return !this.predicate.test(a, handlerMethod);
        }

    }

    class AndMessagePredicate<A extends Message<?>> implements MessagePredicate<A> {

        private final MessagePredicate<A> left;

        private final MessagePredicate<A> right;

        public AndMessagePredicate(MessagePredicate<A> left, MessagePredicate<A> right) {
            Assert.nonNull(left, "Left GatewayPredicate must not be null");
            Assert.nonNull(right, "Right GatewayPredicate must not be null");
            this.left = left;
            this.right = right;
        }


        @Override
        public boolean test(A a, HandlerMethod handlerMethod) {
            return left.test(a, handlerMethod) && right.test(a, handlerMethod);
        }

    }

    class OrMessagePredicate<A extends Message<?>> implements MessagePredicate<A> {

        private final MessagePredicate<A> left;

        private final MessagePredicate<A> right;

        public OrMessagePredicate(MessagePredicate<A> left, MessagePredicate<A> right) {
            Assert.nonNull(left, "Left GatewayPredicate must not be null");
            Assert.nonNull(right, "Right GatewayPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(A a, HandlerMethod handlerMethod) {
            return (this.left.test(a, handlerMethod) || this.right.test(a, handlerMethod));
        }

    }

}
