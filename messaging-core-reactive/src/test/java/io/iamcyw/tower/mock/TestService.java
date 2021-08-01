package io.iamcyw.tower.mock;

import io.iamcyw.tower.commandhandling.CommandHandler;
import io.iamcyw.tower.queryhandling.QueryHandler;
import org.slf4j.LoggerFactory;

public class TestService {

    // @CommandHandler
    // public void command(TestCommand command) {
    //     LoggerFactory.getLogger(TestService.class).info("command: " + command.getId());
    // }

    @CommandHandler
    public String command(TestCommand command) {
        LoggerFactory.getLogger(TestService.class).info("command: " + command.getId());

        return command.getId();
    }

    @QueryHandler
    public String query(TestQuery query) {
        return query.getId() + " result";
    }

}
