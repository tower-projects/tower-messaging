package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.messaging.CommandHandle;
import io.iamcyw.tower.messaging.Parameter;
import io.iamcyw.tower.messaging.Predicate;
import io.iamcyw.tower.messaging.UseCase;
import io.iamcyw.tower.quarkus.it.domain.BasicTestCommand;
import io.iamcyw.tower.utils.CommonKit;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

@UseCase
public class TestVoidService {

    private static final Logger LOGGER = Logger.getLogger(TestVoidService.class);

    @CommandHandle
    @Parameter(value = "test", parameter = "payload")
    public void command(BasicTestCommand basicTestCommand) {
        LOGGER.info(CommonKit.arrayFormat("command: {} payload: {}", basicTestCommand.getClass().getName(),
                                          basicTestCommand.getPayload()));
        Assertions.assertNotNull(basicTestCommand);
        Assertions.assertEquals(basicTestCommand.getPayload(), "payload");
    }

    @Predicate
    public boolean testPredicate(BasicTestCommand payload, @Parameter("test") String type) {
        return payload.getPayload().equals(type);
    }

}
