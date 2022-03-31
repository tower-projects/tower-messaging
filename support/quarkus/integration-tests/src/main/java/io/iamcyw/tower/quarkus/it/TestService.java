package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.commandhandling.CommandHandle;
import io.iamcyw.tower.quarkus.it.domain.BasicTestCommand;
import io.iamcyw.tower.quarkus.it.domain.BasicTestQuery;
import io.iamcyw.tower.queryhandling.QueryHandle;
import io.iamcyw.tower.utils.CommonKit;
import io.iamcyw.tower.utils.lang.NonNull;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
@GraphQLApi
public class TestService {

    private static final Logger LOGGER = Logger.getLogger(TestService.class);

    @CommandHandle
    public String command(@NonNull BasicTestCommand basicTestCommand) {
        LOGGER.info(CommonKit.arrayFormat("command: {} payload: {}", basicTestCommand.getClass().getName(),
                                          basicTestCommand.getPayload()));
        Assertions.assertNotNull(basicTestCommand);
        Assertions.assertEquals(basicTestCommand.getPayload(), "payload");
        return basicTestCommand.getPayload() + "-reply";
    }

    @Query("basicTestQuery")
    @QueryHandle
    public String basicTestQuery(BasicTestQuery query) {
        return "ok";
    }

    @Query("basicTestQuery")
    @QueryHandle
    public List<String> basicTestQuerys(BasicTestQuery query) {
        return List.of("ok");
    }

}
