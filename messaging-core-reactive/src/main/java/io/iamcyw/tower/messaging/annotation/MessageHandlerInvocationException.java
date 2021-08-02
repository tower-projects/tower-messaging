package io.iamcyw.tower.messaging.annotation;

import io.iamcyw.tower.exception.SystemIllegalException;


/**
 * MessageHandlerInvocationException is a runtime exception that wraps an exception thrown by an invoked message
 * handler.
 */
public class MessageHandlerInvocationException extends SystemIllegalException {

    private static final long serialVersionUID = 664867158607341533L;

    /**
     * Initialize the MessageHandlerInvocationException using given {@code message} and {@code cause}.
     *
     * @param message A message describing the cause of the exception
     * @param cause   The exception thrown by the Event Handler
     */
    public MessageHandlerInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

}
