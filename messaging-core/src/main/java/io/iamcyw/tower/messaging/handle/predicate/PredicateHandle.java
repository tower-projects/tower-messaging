package io.iamcyw.tower.messaging.handle.predicate;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.helper.OperationInvoker;
import io.iamcyw.tower.schema.model.Operation;

import java.util.concurrent.CompletableFuture;

public class PredicateHandle implements HandlePredicate {
    private final Operation operation;

    private final OperationInvoker invoker;

    private String identifier;

    public PredicateHandle(Operation operation, Operation target) {
        this.operation = operation;
        this.identifier = operation.getName();
        this.invoker = new PredicateInvoker(operation, target);
    }

    @Override
    public CompletableFuture<Boolean> predicate(Message<?> message) {
        return invoker.invoke(message);
    }

    public String getIdentifier() {
        return identifier;
    }

}
