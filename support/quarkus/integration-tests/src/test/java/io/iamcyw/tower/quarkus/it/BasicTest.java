package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.commandhandling.gateway.CommandGateway;
import io.iamcyw.tower.quarkus.it.domain.BasicTestCommand;
import io.iamcyw.tower.queryhandling.gateway.QueryGateway;
import io.iamcyw.tower.responsetype.ResponseTypes;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
class BasicTest {

    @Inject
    CommandGateway commandGateway;

    @Inject
    QueryGateway queryGateway;

    @Test
    void testBasicVoidCommand() {
        commandGateway.send(new BasicTestCommand("payload"));
    }

    @Test
    void testBasicCommand() {
        String reply = commandGateway.request(new BasicTestCommand("payload"), ResponseTypes.instanceOf(String.class));
        Assertions.assertThat(reply).isEqualTo("payload-reply");
    }

}
