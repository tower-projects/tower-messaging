package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.config.DefaultReactorConfigure;
import io.iamcyw.tower.config.ReactorConfigure;
import io.iamcyw.tower.mock.TestQuery;
import io.iamcyw.tower.mock.TestService;
import org.junit.jupiter.api.Test;

class ReactorQueryBusTest {

    @Test
    void testCommand() {
        ReactorConfigure configure = new DefaultReactorConfigure();

        configure.registerQueryHandler(config -> new TestService());

        ReactorQueryBus queryBus = configure.buildConfiguration().queryBus();

        configure.start();

        queryBus.query(new GenericQueryMessage(new TestQuery("id"))).subscribe().with(System.out::println);
    }

}