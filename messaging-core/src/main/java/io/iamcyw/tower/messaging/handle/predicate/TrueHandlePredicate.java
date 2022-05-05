package io.iamcyw.tower.messaging.handle.predicate;

import io.iamcyw.tower.messaging.Message;

import java.util.concurrent.CompletableFuture;

public class TrueHandlePredicate implements HandlePredicate {
    @Override
    public CompletableFuture<Boolean> predicate(Message<?> message) {
        return CompletableFuture.completedFuture(true);
    }

}
