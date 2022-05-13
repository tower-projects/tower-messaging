package io.iamcyw.tower.messaging.handle.predicate;

import io.iamcyw.tower.messaging.handle.helper.ArgumentHelper;
import io.iamcyw.tower.messaging.handle.helper.OperationInvoker;
import io.iamcyw.tower.schema.model.Operation;

public class PredicateInvoker extends OperationInvoker {

    public PredicateInvoker(Operation operation, Operation target) {
        super(operation, new ArgumentHelper(operation.getArguments(), (argument, message) -> {
            if (argument.isParameterArgument()) {
                return target.getParameter(argument.getName());
            }
            return null;
        }));
    }

}
