package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.commandhandling.CommandHandle;
import io.iamcyw.tower.quarkus.it.domain.BasicTestCommand;
import io.iamcyw.tower.utils.CommonKit;
import io.iamcyw.tower.utils.lang.NonNull;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestVoidService {

    private static final Logger LOGGER = Logger.getLogger(TestVoidService.class);

    @CommandHandle
    public void command(@NonNull BasicTestCommand basicTestCommand) {
        LOGGER.info(CommonKit.arrayFormat("command: {} payload: {}", basicTestCommand.getClass().getName(),
                                          basicTestCommand.getPayload()));
        Assertions.assertNotNull(basicTestCommand);
        Assertions.assertEquals(basicTestCommand.getPayload(), "payload");
    }

}
