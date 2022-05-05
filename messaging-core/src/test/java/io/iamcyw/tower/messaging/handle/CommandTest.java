package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.messaging.test.TestQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandTest extends ExecutionTestBase {

    @Test
    void testQuery() {
        String result = messageGateway.query(new TestQuery(), String.class);
        Assertions.assertThat(result).isEqualTo("success");
    }

}
