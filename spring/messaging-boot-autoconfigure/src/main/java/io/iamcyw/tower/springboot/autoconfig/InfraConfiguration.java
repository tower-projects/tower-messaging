package io.iamcyw.tower.springboot.autoconfig;

import io.iamcyw.tower.spring.config.AnnotationDriven;
import io.iamcyw.tower.spring.config.SpringTowerAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ConditionalOnClass(SpringTowerAutoConfiguration.class)
@AutoConfigureAfter(
        {TowerAutoConfiguration.class, NoOpTransactionAutoConfiguration.class, TransactionAutoConfiguration.class})
@Import(SpringTowerAutoConfiguration.ImportSelector.class)
@AnnotationDriven
@Configuration
public class InfraConfiguration {
}
