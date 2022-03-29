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

    @Singleton
    @Produces
    public QueryGateway queryGateway;

    @Singleton
    @Produces
    public CommandGateway commandGateway;

    @Singleton
    @Produces
    public DomainNameMappings domainMaps;

    @Inject
    Instance<MessageDispatchInterceptor> messageDispatchInterceptors;

    @Inject
    Instance<MessageHandlerInterceptor<?>> messageHandlerInterceptors;

    public void setQueryBus(QueryBus queryBus) {
        messageHandlerInterceptors.forEach(queryBus::registerHandlerInterceptor);
        this.queryGateway = new DefaultQueryGateway(queryBus,
                                                    messageDispatchInterceptors.stream().collect(Collectors.toList()));
    }

    public void setCommandBus(CommandBus commandBus) {
        messageHandlerInterceptors.forEach(commandBus::registerHandlerInterceptor);
        this.commandGateway = new DefaultCommandGateway(commandBus, messageDispatchInterceptors.stream()
                                                                                               .collect(
                                                                                                       Collectors.toList()));
    }

    public void setDomainMaps(Map<String, Class<?>> domainMaps) {
        this.domainMaps = new DomainNameMappings(domainMaps);
    }

}
