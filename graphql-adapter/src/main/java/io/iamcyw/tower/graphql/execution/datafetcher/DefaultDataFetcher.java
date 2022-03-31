package io.iamcyw.tower.graphql.execution.datafetcher;

import io.smallrye.graphql.schema.model.Operation;

public class DefaultDataFetcher<K, T> extends io.smallrye.graphql.execution.datafetcher.DefaultDataFetcher<K, T> {
    public DefaultDataFetcher(Operation operation) {
        super(operation);
        operationInvoker = new GatewayOperationInvoker(operation);
    }

}
