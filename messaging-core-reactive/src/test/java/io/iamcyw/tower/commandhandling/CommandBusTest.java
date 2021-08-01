package io.iamcyw.tower.commandhandling;

import io.iamcyw.tower.config.DefaultReactorConfigure;
import io.iamcyw.tower.config.ReactorConfigure;
import io.iamcyw.tower.mock.TestCommand;
import io.iamcyw.tower.mock.TestService;
import org.junit.jupiter.api.Test;

public class CommandBusTest {

    @Test
    void testCommand() {
        ReactorConfigure configure = new DefaultReactorConfigure();

        configure.registerCommandHandler(config -> new TestService());

        ReactorCommandBus commandBus = configure.buildConfiguration().commandBus();

        configure.start();

        commandBus.<TestCommand, String>dispatch(GenericCommandMessage.asCommandMessage(new TestCommand("id11")))
                  .subscribe().with(s -> System.out.println(s));
    }

}
