package io.iamcyw.tower.common;

public class IllegalPayloadAccessException extends SystemNonTransientException {

    /**
     * Creates the exception with given {@code message}.
     *
     * @param message the exception message
     */
    public IllegalPayloadAccessException(String message) {
        super(message);
    }

    /**
     * Creates the exception with given {@code message} and {@code cause}.
     *
     * @param message the exception message
     * @param cause   the exception which caused illegal payload access
     */
    public IllegalPayloadAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
