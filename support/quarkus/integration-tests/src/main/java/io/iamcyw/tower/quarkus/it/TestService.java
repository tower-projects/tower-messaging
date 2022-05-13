package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.messaging.*;
import io.iamcyw.tower.quarkus.it.domain.BasicTestCommand;
import io.iamcyw.tower.quarkus.it.domain.BasicTestQuery;
import io.iamcyw.tower.utils.CommonKit;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

@UseCase
public class TestService {

    private static final Logger LOGGER = Logger.getLogger(TestService.class);

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

    @QueryHandle
    @Parameter(value = "test", parameter = "payload")
    public String query(BasicTestQuery basicTestQuery) {
        return "success";
    }

}
