package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.quarkus.it.domain.TestCommand;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class ServiceTest {

    @Inject
    CommandGateway commandGateway;

    @Test
    public void testService() {
        commandGateway.send(new TestCommand("test"));
    }

}
