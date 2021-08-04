package io.iamcyw.tower.queryhandling;

import io.iamcyw.tower.config.DefaultReactorConfigure;
import io.iamcyw.tower.config.ReactorConfigure;
import io.iamcyw.tower.mock.TestQuery;
import io.iamcyw.tower.mock.TestService;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import org.junit.jupiter.api.Test;

class ReactorQueryBusTest {

    @Test
    void testCommand() {
        ReactorConfigure configure = new DefaultReactorConfigure();

        configure.registerQueryHandler(config -> new TestService());

        ReactorQueryBus queryBus = configure.buildConfiguration().queryBus();

        configure.start();

        AssertSubscriber<String> subscriber = queryBus.<String>query(new GenericQueryMessage(new TestQuery("id")))
                                                      .subscribe().withSubscriber(AssertSubscriber.create(1));

        subscriber.assertCompleted().assertItems("id result");
    }

}