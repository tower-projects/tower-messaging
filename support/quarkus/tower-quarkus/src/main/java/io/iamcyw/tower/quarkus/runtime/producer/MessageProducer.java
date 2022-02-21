package io.iamcyw.tower.quarkus.runtime.producer;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.commandhandling.gateway.DefaultCommandGateway;
import io.iamcyw.tower.messaging.interceptor.MessageDispatchInterceptor;
import io.iamcyw.tower.messaging.interceptor.MessageHandlerInterceptor;
import io.iamcyw.tower.quarkus.runtime.DomainNameMappings;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.gateway.DefaultQueryGateway;
import io.iamcyw.tower.queryhandling.gateway.QueryGateway;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class MessageProducer {

    public QueryBus queryBus;

    public CommandBus commandBus;

    public Map<String, Class<?>> domainMaps;

    @Inject
    Instance<MessageDispatchInterceptor> messageDispatchInterceptors;

    @Inject
    Instance<MessageHandlerInterceptor> messageHandlerInterceptors;

    public void setQueryBus(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    public void setCommandBus(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    public void setDomainMaps(Map<String, Class<?>> domainMaps) {
        this.domainMaps = domainMaps;
    }

    @Singleton
    @Produces
    public DomainNameMappings domainNameMappings() {
        return new DomainNameMappings(this.domainMaps);
    }

    @Singleton
    @Produces
    public CommandGateway commandGateway() {
        messageHandlerInterceptors.forEach(interceptor -> commandBus.registerHandlerInterceptor(interceptor));
        return new DefaultCommandGateway(commandBus, messageDispatchInterceptors.stream().collect(Collectors.toList()));
    }

    @Singleton
    @Produces
    public QueryGateway queryGateway() {
        messageHandlerInterceptors.forEach(interceptor -> queryBus.registerHandlerInterceptor(interceptor));
        return new DefaultQueryGateway(queryBus, messageDispatchInterceptors.stream().collect(Collectors.toList()));
    }

}
