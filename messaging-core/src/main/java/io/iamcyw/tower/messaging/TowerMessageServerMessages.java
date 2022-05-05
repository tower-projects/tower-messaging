package io.iamcyw.tower.messaging;

import io.iamcyw.tower.messaging.handle.Identifier;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

import java.time.DateTimeException;
import java.util.NoSuchElementException;

@MessageBundle(projectCode = "MESSAGE")
public interface TowerMessageServerMessages {
    TowerMessageServerMessages msg = Messages.getBundle(TowerMessageServerMessages.class);

    @Message(id = 1, value = "Unknown primitive type [%s]")
    ClassNotFoundException unknownPrimitiveType(String name);

    @Message(id = 3, value = "Message Handling failed for [%s]")
    RuntimeException generalMessageHandleException(String operation, @Cause Throwable cause);

    @Message(id = 4, value = "No matches found for [%s]")
    NoSuchElementException notMatchHandleException(Identifier identifier);

    @Message(id = 5, value = "Could not get Instance using the default lookup service")
    RuntimeException countNotGetInstance(@Cause Throwable t);

    @Message(id = 6, value = "Metrics are not supported without CDI")
    UnsupportedOperationException metricsNotSupportedWithoutCDI();

    @Message(id = 8, value = "OpenTracing is not supported without CDI")
    UnsupportedOperationException openTracingNotSupportedWithoutCDI();

    @Message(id = 9, value = "Can not load class [%s]")
    RuntimeException canNotLoadClass(String className, @Cause Exception cause);

    @Message(id = 10, value = "[%s] is not a valid number type")
    RuntimeException notAValidNumberType(String typeClassName);

    @Message(id = 11, value = "Can not parse a number from [%s]")
    NumberFormatException numberFormatException(String input);


    @Message(id = 16, value = "Can't parse [%s] into [%s]")
    RuntimeException cantParseDate(String inputTypeName, String targetClassName);

    @Message(id = 17, value = "[%s] is no valid date or time-type")
    RuntimeException notValidDateOrTimeType(String className);

    @Message(id = 18, value = "Unknown date format [%s]")
    DateTimeException unknownDateFormat(String input);

    @Message(id = 19, value = "Unsupported wrapped type. SmallRye only support DataFetchingEnvironment and not %s")
    IllegalArgumentException unsupportedWrappedClass(String className);

    @Message(id = 20,
             value = "Can not inject an instance of class [%s]. Please make sure it is a CDI bean, also possibly the " +
                     "beans.xml file is needed")
    RuntimeException canNotInjectClass(String className, @Cause Exception cause);


}
