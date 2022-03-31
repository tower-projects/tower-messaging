package io.iamcyw.tower.graphql.execution.datafetcher;

import io.iamcyw.tower.queryhandling.gateway.QueryGateway;
import io.iamcyw.tower.responsetype.ResponseType;
import io.iamcyw.tower.responsetype.ResponseTypes;
import io.smallrye.graphql.execution.datafetcher.helper.OperationInvoker;
import io.smallrye.graphql.schema.model.Operation;
import io.smallrye.graphql.schema.model.Wrapper;

public class GatewayOperationInvoker extends OperationInvoker {
    protected final QueryGatewayLookupService gatewayLookupService = QueryGatewayLookupService.get();

    protected final Wrapper wrapper;

    public GatewayOperationInvoker(Operation operation) {
        super(operation);
        wrapper = operation.getWrapper();
    }

    @Override
    public <T> T invoke(Object... arguments) throws Exception {

        ResponseType responseType = ResponseTypes.instanceOf(Object.class);
        // TODO: Context propagation ?
        if (wrapper != null) {
            if (wrapper.isArray() || wrapper.isCollection()) {
                responseType = ResponseTypes.multipleInstancesOf(Object.class);
            }
        }

        QueryGateway queryGateway = gatewayLookupService.getQueryGateway();
        return (T) queryGateway.query(arguments, responseType);
    }

}
