package io.iamcyw.tower.queryhandling.gateway;

import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.interceptor.MessageDispatchInterceptor;
import io.iamcyw.tower.queryhandling.QueryBus;

import java.util.List;

public class DefaultQueryGateway implements QueryGateway {
    private final QueryBus queryBus;

    private final List<MessageDispatchInterceptor> interceptors;

    public DefaultQueryGateway(QueryBus queryBus, List<MessageDispatchInterceptor> interceptors) {
        this.queryBus = queryBus;
        this.interceptors = interceptors;
    }

    @Override
    public <R> R query(Object query) {
        return queryBus.<R>dispatch(wrapperMessage(query)).join();
    }

    @Override
    public <R> List<R> queries(Object query) {
        return queryBus.<List<R>>dispatch(wrapperMessage(query)).join();
    }

    private Message wrapperMessage(Object payload) {
        Message message = new GenericMessage(payload);
        for (MessageDispatchInterceptor interceptor : interceptors) {
            message = interceptor.handle(message);
        }
        return message;
    }

}
