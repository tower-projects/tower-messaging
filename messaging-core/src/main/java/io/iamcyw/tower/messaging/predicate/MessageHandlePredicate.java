package io.iamcyw.tower.messaging.predicate;

import io.iamcyw.tower.messaging.Message;

@FunctionalInterface
public interface MessageHandlePredicate {
    boolean test(Message message);

    default MessageHandlePredicate and(MessageHandlePredicate other) {
        return new AndMessageHandlePredicate(this, other);
    }

    class AndMessageHandlePredicate implements MessageHandlePredicate {
        public MessageHandlePredicate current;

        public MessageHandlePredicate other;

        public AndMessageHandlePredicate() {

        }

        public AndMessageHandlePredicate(MessageHandlePredicate current, MessageHandlePredicate other) {
            this.current = current;
            this.other = other;
        }

        @Override
        public boolean test(Message message) {
            return current.test(message) && other.test(message);
        }

    }

}