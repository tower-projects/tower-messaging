package io.iamcyw.tower;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "TOWER-COMMON")
public interface TowerMessageCommonMessages {

    TowerMessageCommonMessages log = Messages.getBundle(TowerMessageCommonMessages.class);

    @Message(id = 1, value = "Unknown primitive type [%s]")
    ClassNotFoundException unknownPrimitiveType(String name);

    @Message(id = 2, value = "Object required to be not null")
    IllegalArgumentException objectRequiredNotNull();

    @Message(id = 3, value = "Parameter '%s' may not be null")
    IllegalArgumentException nullParam(String param);

    @Message(id = 4, value = "cfFactory must return a non null value")
    String cfFactoryNonNullValue();

}
