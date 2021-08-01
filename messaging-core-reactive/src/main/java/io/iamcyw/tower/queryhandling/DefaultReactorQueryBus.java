package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.messaging.responsetypes.ResponseType;
import io.smallrye.mutiny.Multi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DefaultReactorQueryBus implements ReactorQueryBus {

    private final QueryMessageHandles handles;

    private final List<ReactorQueryFilter> handlerInterceptors = new CopyOnWriteArrayList<>();

    public DefaultReactorQueryBus(QueryMessageHandles handles) {
        this.handles = handles;
    }

    @Override
    public <Q, R> Multi<QueryResponseMessage<R>> query(QueryMessage<Q, R> queryMessage) {
        return filter(queryMessage, q -> lookupHandler(q).toUni().onItem().transformToMulti(
                handle -> handle.handle(q).map(r -> this.buildResponseMessage(q.getResponseType(), r))));
    }

    @Override
    public <Q, R> Multi<QueryResponseMessage<R>> scatterGather(QueryMessage<Q, R> queryMessage, long timeout,
                                                               TimeUnit unit) {
        return filter(queryMessage, q -> lookupHandler(q).onItem().transformToMultiAndConcatenate(
                handle -> handle.handle(q).map(r -> this.buildResponseMessage(q.getResponseType(), r))));
    }

    private <R> QueryResponseMessage<R> buildResponseMessage(ResponseType<R> responseType, Object queryResponse) {
        return GenericQueryResponseMessage.asNullableResponseMessage(responseType.responseMessagePayloadType(),
                                                                     responseType.convert(queryResponse));
    }

    <Q, R> Multi<QueryResponseMessage<R>> filter(QueryMessage<Q, R> queryMessage,
                                                 Function<QueryMessage<Q, R>, Multi<QueryResponseMessage<R>>> target) {
        return new DefaultReactorQueryFilterChain(handlerInterceptors).filter(queryMessage, target);
    }

    <Q, R> Multi<QueryMessageHandler<Q, R>> lookupHandler(QueryMessage<Q, R> queryMessage) {
        return handles.getHandles(queryMessage).filter(messageHandler -> messageHandler.canHandle(queryMessage));
    }


}
