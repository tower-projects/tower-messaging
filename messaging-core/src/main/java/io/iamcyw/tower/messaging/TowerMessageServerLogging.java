package io.iamcyw.tower.messaging;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "MESSAGE")
public interface TowerMessageServerLogging {

    // numbers reserved for this module are 10000-19999

    TowerMessageServerLogging log = Logger.getMessageLogger(TowerMessageServerLogging.class,
                                                            TowerMessageServerLogging.class.getPackage().getName());



    /* 13000-13999: service related logs (CDI, Tracing, Metrics,...) */

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 13003, value = "Using %s service for object lookups")
    void usingLookupService(String name);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 13004, value = "Using %s service for class loading")
    void usingClassLoadingService(String name);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 13005, value = "Using %s service for MethodInvokeService")
    void usingMethodInvokeService(String name);

}
