package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.messaging.gateway.MessageGateway;
import io.iamcyw.tower.quarkus.it.domain.BasicTestQuery;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
class BasicTest {

    @Inject
    MessageGateway commandGateway;

    @Test
    void testBasicVoidCommand() {
        // commandGateway.send(new BasicTestCommand("payload"));

        String result = commandGateway.query(new BasicTestQuery("payload"), String.class);
        Assertions.assertThat(result).isNotBlank();
    }

}
