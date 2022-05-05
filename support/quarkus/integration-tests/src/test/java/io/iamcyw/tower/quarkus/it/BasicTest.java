package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.messaging.gateway.MessageGateway;
import io.iamcyw.tower.quarkus.it.domain.BasicTestCommand;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
class BasicTest {

    @Inject
    MessageGateway commandGateway;

    @Test
    void testBasicVoidCommand() {
        commandGateway.send(new BasicTestCommand("payload"));
    }

}
