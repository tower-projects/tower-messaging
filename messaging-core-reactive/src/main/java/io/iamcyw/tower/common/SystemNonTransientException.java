package io.iamcyw.tower.common;

import io.iamcyw.tower.exception.SystemIllegalException;

/**
 * Exception indicating an error has been cause that cannot be resolved without intervention. Retrying the operation
 * that threw the exception will most likely result in the same exception being thrown.
 * <p/>
 * Examples of such errors are programming errors and version conflicts.
 */
public abstract class SystemNonTransientException extends SystemIllegalException {

    private static final long serialVersionUID = -2119569988731244940L;

    /**
     * Initializes the exception using the given {@code message}.
     *
     * @param message The message describing the exception
     */
    public SystemNonTransientException(String message) {
        super(message);
    }

    /**
     * Initializes the exception using the given {@code message} and {@code cause}.
     *
     * @param message The message describing the exception
     * @param cause   The underlying cause of the exception
     */
    public SystemNonTransientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Indicates whether the given {@code throwable} is a AxonNonTransientException exception or indicates to be
     * caused by one.
     *
     * @param throwable The throwable to inspect
     * @return {@code true} if the given instance or one of it's causes is an instance of
     * AxonNonTransientException, otherwise {@code false}
     */
    public static boolean isCauseOf(Throwable throwable) {
        return throwable != null &&
                (throwable instanceof SystemNonTransientException || isCauseOf(throwable.getCause()));
    }

}
