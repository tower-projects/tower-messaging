package io.iamcyw.tower.springboot.autoconfig;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.commandhandling.SimpleCommandBus;
import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.commandhandling.gateway.DefaultCommandGateway;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.messaging.correlation.CorrelationDataProvider;
import io.iamcyw.tower.messaging.correlation.MessageOriginProvider;
import io.iamcyw.tower.messaging.interceptors.CorrelationDataInterceptor;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryInvocationErrorHandler;
import io.iamcyw.tower.queryhandling.SimpleQueryBus;
import io.iamcyw.tower.spring.config.TowerConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@AutoConfigureAfter(
        name = {"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration", "org.axonframework" +
                ".boot.autoconfig.JpaAutoConfiguration"})
public class TowerAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public CorrelationDataProvider messageOriginProvider() {
        return new MessageOriginProvider();
    }

    @ConditionalOnMissingBean
    @Bean
    public CommandGateway commandGateway(CommandBus commandBus) {
        return new DefaultCommandGateway(commandBus);
    }


    @ConditionalOnMissingBean(value = CommandBus.class)
    @Qualifier("localSegment")
    @Bean
    public SimpleCommandBus commandBus(TransactionManager txManager, TowerConfiguration axonConfiguration) {
        SimpleCommandBus commandBus = new SimpleCommandBus(txManager, axonConfiguration
                .messageMonitor(CommandBus.class, "commandBus"));
        commandBus.registerHandlerInterceptor(
                new CorrelationDataInterceptor<>(axonConfiguration.correlationDataProviders()));
        return commandBus;
    }

    @ConditionalOnMissingBean(value = {QueryBus.class, QueryInvocationErrorHandler.class})
    @Qualifier("localSegment")
    @Bean
    public SimpleQueryBus queryBus(TowerConfiguration axonConfiguration, TransactionManager transactionManager) {
        return new SimpleQueryBus(axonConfiguration.messageMonitor(QueryBus.class, "queryBus"), transactionManager,
                                  axonConfiguration.getComponent(QueryInvocationErrorHandler.class));
    }

    @ConditionalOnBean(QueryInvocationErrorHandler.class)
    @ConditionalOnMissingBean(QueryBus.class)
    @Qualifier("localSegment")
    @Bean
    public SimpleQueryBus queryBus(TowerConfiguration axonConfiguration, TransactionManager transactionManager,
                                   QueryInvocationErrorHandler eh) {
        return new SimpleQueryBus(axonConfiguration.messageMonitor(QueryBus.class, "queryBus"), transactionManager, eh);
    }

}
