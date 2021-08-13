package io.iamcyw.tower.messaging.predicate;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.utils.Assert;

import java.util.function.Predicate;

public interface MessagePredicate<T extends Message> extends Predicate<T> {

    static <A extends Message> MessagePredicate<A> wrapIfNeeded(Predicate<? super A> other) {
        MessagePredicate<A> right;

        if (other instanceof MessagePredicate) {
            right = (MessagePredicate<A>) other;
        } else {
            right = new MessagePredicateWrapper(other);
        }
        return right;
    }

    @Override
    default Predicate<T> and(Predicate<? super T> other) {
        return new AndMessagePredicate<>(this, wrapIfNeeded(other));
    }

    @Override
    default Predicate<T> negate() {
        return new NegateMessagePredicate<>(this);
    }

    @Override
    default Predicate<T> or(Predicate<? super T> other) {
        return new OrMessagePredicate<>(this, wrapIfNeeded(other));
    }

    class MessagePredicateWrapper<A extends Message> implements MessagePredicate<A> {

        private final Predicate<A> delegate;

        public MessagePredicateWrapper(Predicate<A> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean test(A a) {
            return delegate.test(a);
        }

        @Override
        public String toString() {
            return this.delegate.getClass().getSimpleName();
        }

    }

    class NegateMessagePredicate<A extends Message> implements MessagePredicate<A> {

        private final MessagePredicate<A> predicate;

        public NegateMessagePredicate(MessagePredicate<A> predicate) {
            Assert.nonNull(predicate, "predicate GatewayPredicate must not be null");
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a) {
            return !this.predicate.test(a);
        }

    }

    class AndMessagePredicate<A extends Message> implements MessagePredicate<A> {

        private final MessagePredicate<A> left;

        private final MessagePredicate<A> right;

        public AndMessagePredicate(MessagePredicate<A> left, MessagePredicate<A> right) {
            Assert.nonNull(left, "Left GatewayPredicate must not be null");
            Assert.nonNull(right, "Right GatewayPredicate must not be null");
            this.left = left;
            this.right = right;
        }


        @Override
        public boolean test(A a) {
            return left.test(a) && right.test(a);
        }

    }

    class OrMessagePredicate<A extends Message> implements MessagePredicate<A> {

        private final MessagePredicate<A> left;

        private final MessagePredicate<A> right;

        public OrMessagePredicate(MessagePredicate<A> left, MessagePredicate<A> right) {
            Assert.nonNull(left, "Left GatewayPredicate must not be null");
            Assert.nonNull(right, "Right GatewayPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(A a) {
            return (this.left.test(a) || this.right.test(a));
        }

    }

}
