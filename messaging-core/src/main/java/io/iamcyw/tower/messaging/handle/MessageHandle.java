package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.helper.OperationInvoker;
import io.iamcyw.tower.messaging.handle.predicate.HandlePredicate;
import io.iamcyw.tower.messaging.handle.predicate.PredicateHandle;
import io.iamcyw.tower.schema.model.Operation;

import java.util.concurrent.CompletableFuture;

public class MessageHandle<R> {

    private final Operation operation;

    private final Identifier identifier;

    private HandlePredicate predicate = message -> CompletableFuture.completedFuture(true);

    private OperationInvoker invoker;

    public MessageHandle(Operation operation) {
        this.operation = operation;
        this.identifier = new Identifier(operation);
        this.invoker = new OperationInvoker(operation);
    }

    public <R> CompletableFuture<R> handle(Message<R> message) {
        return invoker.invoke(message);
    }

    public boolean predicate(Message message) {
        return predicate.predicate(message).join();
    }

    public void addPredicate(HandlePredicate predicate) {
        this.predicate = this.predicate.and(predicate);
    }

    public void addPredicate(Operation predicate) {
        addPredicate(new PredicateHandle(predicate, this.operation));
    }

    public Operation getOperation() {
        return operation;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

}
