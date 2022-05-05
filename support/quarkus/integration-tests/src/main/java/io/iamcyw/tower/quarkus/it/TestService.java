package io.iamcyw.tower.quarkus.it;

import org.jboss.logging.Logger;

public class TestService {

    private static final Logger LOGGER = Logger.getLogger(TestService.class);

    // @CommandHandle
    // public String command(BasicTestCommand basicTestCommand) {
    //     LOGGER.info(CommonKit.arrayFormat("command: {} payload: {}", basicTestCommand.getClass().getName(),
    //                                       basicTestCommand.getPayload()));
    //     Assertions.assertNotNull(basicTestCommand);
    //     Assertions.assertEquals(basicTestCommand.getPayload(), "payload");
    //     return basicTestCommand.getPayload() + "-reply";
    // }
    //
    // @Query("basicTestQuery")
    // @QueryHandle
    // public String basicTestQuery(basictest query) {
    //     return "ok";
    // }
    //
    // @Query("basicTestQuery")
    // @QueryHandle
    // public List<String> basicTestQuerys(BasicTestQuery query) {
    //     return List.of("ok");
    // }

}
