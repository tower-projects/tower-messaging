package io.iamcyw.tower.queryhandling.gateway;

import io.iamcyw.tower.messaging.GenericMessage;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MetaData;
import io.iamcyw.tower.messaging.interceptor.MessageDispatchInterceptor;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.responsetype.ResponseType;

import java.util.List;

public class DefaultQueryGateway implements QueryGateway {
    private final QueryBus queryBus;

    private final List<MessageDispatchInterceptor> interceptors;

    public DefaultQueryGateway(QueryBus queryBus, List<MessageDispatchInterceptor> interceptors) {
        this.queryBus = queryBus;
        this.interceptors = interceptors;
    }

    @Override
    public <R> R query(Object query, ResponseType<R> responseType) {
        return queryBus.<R>dispatch(wrapperMessage(query, responseType)).join();
    }

    private <R> Message wrapperMessage(Object payload, ResponseType<R> responseType) {
        MetaData metaData = new MetaData();
        metaData.setResponseType(responseType);
        Message message = new GenericMessage(metaData, payload);
        for (MessageDispatchInterceptor interceptor : interceptors) {
            message = interceptor.handle(message);
        }
        return message;
    }


}
