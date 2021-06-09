package io.iamcyw.tower.springboot.autoconfig;

import io.iamcyw.tower.commandhandling.CommandBus;
import io.iamcyw.tower.commandhandling.DuplicateCommandHandlerResolver;
import io.iamcyw.tower.commandhandling.LoggingDuplicateCommandHandlerResolver;
import io.iamcyw.tower.commandhandling.SimpleCommandBus;
import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.commandhandling.gateway.DefaultCommandGateway;
import io.iamcyw.tower.common.transaction.TransactionManager;
import io.iamcyw.tower.messaging.interceptors.CorrelationDataInterceptor;
import io.iamcyw.tower.queryhandling.DefaultQueryGateway;
import io.iamcyw.tower.queryhandling.QueryBus;
import io.iamcyw.tower.queryhandling.QueryGateway;
import io.iamcyw.tower.spring.config.TowerConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class TowerAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public CommandGateway commandGateway(CommandBus commandBus) {
        return DefaultCommandGateway.builder()
                .commandBus(commandBus)
                .build();
    }

    @ConditionalOnMissingBean
    @Bean
    public QueryGateway queryGateway(QueryBus queryBus) {
        return DefaultQueryGateway.builder()
                .queryBus(queryBus)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public DuplicateCommandHandlerResolver duplicateCommandHandlerResolver() {
        return LoggingDuplicateCommandHandlerResolver.instance();
    }

    @ConditionalOnMissingBean(CommandBus.class)
    @Qualifier("localSegment")
    @Bean
    public SimpleCommandBus commandBus(TransactionManager txManager, TowerConfiguration towerConfiguration, DuplicateCommandHandlerResolver duplicateCommandHandlerResolver) {
        SimpleCommandBus commandBus = SimpleCommandBus.builder()
                .transactionManager(txManager)
                .duplicateCommandHandlerResolver(duplicateCommandHandlerResolver)
                .messageMonitor(towerConfiguration.messageMonitor(CommandBus.class, "commandBus"))
                .build();
        commandBus.registerHandlerInterceptor(
                new CorrelationDataInterceptor<>(towerConfiguration.correlationDataProviders()));
        return commandBus;
    }

}
