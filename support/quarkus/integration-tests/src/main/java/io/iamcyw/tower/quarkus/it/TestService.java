package io.iamcyw.tower.quarkus.it;

import io.iamcyw.tower.commandhandling.CommandHandle;
import io.iamcyw.tower.messaging.predicate.MessagePredicate;
import io.iamcyw.tower.messaging.predicate.PredicateHandle;
import io.iamcyw.tower.quarkus.it.domain.TestCommand;
import io.iamcyw.tower.queryhandling.QueryHandle;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TestService {

    @CommandHandle
    @MessagePredicate(value = "payload", parameter = "test")
    public String command(TestCommand testCommand) {
        return testCommand.getPayload();
    }

    @CommandHandle
    @MessagePredicate(value = "payload", parameter = "b")
    @MessagePredicate(value = "payload")
    public String command1(TestCommand testCommand) {
        return testCommand.getPayload();
    }

    @PredicateHandle("payload")
    public boolean predicate(TestCommand testCommand, String parameter) {
        return Objects.equals(testCommand.payload, parameter);
    }

    @QueryHandle
    public String query(TestCommand testCommand) {
        return testCommand.getPayload();
    }

}
