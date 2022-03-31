package io.iamcyw.tower.graphql;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "TWMSG")
public interface TowerMessageServerLogging {

    TowerMessageServerLogging log = Logger.getMessageLogger(TowerMessageServerLogging.class,
                                                            TowerMessageServerLogging.class.getPackage().getName());

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 13003, value = "Using %s service for object lookups")
    void usingLookupService(String name);


}
