package io.iamcyw.tower.messaging.handle.helper;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.schema.model.Operation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OperationInvoker extends ReflectionInvoker {

    protected final ArgumentHelper argumentHelper;

    public OperationInvoker(Operation operation, ArgumentHelper argumentHelper) {
        super(operation);
        this.argumentHelper = argumentHelper;
    }

    public OperationInvoker(Operation operation) {
        this(operation, new ArgumentHelper(operation.getArguments()));
    }

    public <R, R1> CompletableFuture<R> invoke(Message<R1> message) {
        Object[] arguments = argumentHelper.getArguments(message);

        Object result = invoke(arguments);

        CompletableFuture<R> resultCF;
        if (result instanceof CompletionStage) {
            resultCF = ((CompletionStage<R>) result).toCompletableFuture();
        } else {
            resultCF = CompletableFuture.completedFuture((R) result);
        }
        return resultCF;
    }

}
