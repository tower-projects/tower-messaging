package io.iamcyw.tower.graphql;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "TWMSG")
public interface TowerMessageServerMessages {

    TowerMessageServerMessages msg = Messages.getBundle(TowerMessageServerMessages.class);

    @Message(id = 5, value = "Could not get Instance using the default lookup service")
    RuntimeException countNotGetInstance(@Cause Throwable t);


}
