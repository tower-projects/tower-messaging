package io.iamcyw.tower.messaging.handle.predicate;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface HandlePredicate {
    CompletableFuture<Boolean> predicate(Message<?> message);

    default HandlePredicate and(HandlePredicate other) {
        return new AndHandlePredicate(this, other);
    }

    class AndHandlePredicate implements HandlePredicate {
        public HandlePredicate current;

        public HandlePredicate other;

        public AndHandlePredicate() {

        }

        public AndHandlePredicate(HandlePredicate current, HandlePredicate other) {
            this.current = current;
            this.other = other;
        }

        @Override
        public CompletableFuture<Boolean> predicate(Message<?> message) {
            return current.predicate(message)
                          .thenCombine(other.predicate(message), (current, other) -> current && other);
        }

    }

}