package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.handle.helper.OperationInvoker;
import io.iamcyw.tower.messaging.handle.interceptor.DefaultInterceptorChain;
import io.iamcyw.tower.messaging.handle.interceptor.InterceptorChain;
import io.iamcyw.tower.messaging.handle.interceptor.MessageInterceptor;
import io.iamcyw.tower.messaging.handle.predicate.HandlePredicate;
import io.iamcyw.tower.messaging.handle.predicate.PredicateHandle;
import io.iamcyw.tower.schema.model.Operation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageHandle<R> {

    private final Operation operation;

    private final Identifier identifier;

    private HandlePredicate predicate = message -> CompletableFuture.completedFuture(true);

    private List<MessageInterceptor<R>> messageInterceptors;

    private OperationInvoker invoker;

    public MessageHandle(Operation operation, List<MessageInterceptor<R>> messageInterceptors) {
        this.operation = operation;
        this.messageInterceptors = messageInterceptors;
        this.identifier = new Identifier(operation);
        this.invoker = new OperationInvoker(operation);
    }

    public CompletableFuture<R> handle(Message message) {
        CompletableFuture<InterceptorChain<R>> chainCF = DefaultInterceptorChain.buildChain(messageInterceptors,
                                                                                            () -> invoke(message));

        return chainCF.thenCompose(chain -> chain.filter(message));
    }

    private InterceptorChain<R> invoke(Message message) {
        return msg -> invoker.invoke(message);
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
