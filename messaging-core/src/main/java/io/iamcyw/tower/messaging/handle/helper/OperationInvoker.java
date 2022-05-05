package io.iamcyw.tower.messaging.handle.helper;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.schema.model.Field;
import io.iamcyw.tower.schema.model.Operation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class OperationInvoker extends ReflectionInvoker {

    protected final ArgumentHelper argumentHelper;

    public OperationInvoker(Operation operation) {
        this(operation, operation.getClassName(), new ArgumentHelper(operation.getArguments()));
    }

    public OperationInvoker(Operation operation, String className, ArgumentHelper argumentHelper) {
        super(className);
        super.setMethod(operation.getMethodName(), getParameterClasses(operation));
        this.argumentHelper = argumentHelper;
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


    private List<String> getParameterClasses(Operation operation) {
        if (operation.hasArguments()) {
            List<String> cl = new LinkedList<>();
            for (Field argument : operation.getArguments()) {
                if (argument.hasWrapper()) {
                    cl.add(argument.getWrapper().getWrapperClassName());
                } else {
                    cl.add(argument.getReference().getClassName());
                }
            }
            return cl;
        }
        return null;
    }

}
