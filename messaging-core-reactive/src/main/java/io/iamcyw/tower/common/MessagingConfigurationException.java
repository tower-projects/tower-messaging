package io.iamcyw.tower.common;

/**
 * Exception indicating that a configuration error has been made in the Axon configuration. This problem prevents the
 * application from operating properly.
 *
 * @author Allard Buijze
 * @since 0.7
 */
public class MessagingConfigurationException extends SystemNonTransientException {

    private static final long serialVersionUID = 4369033925877210475L;

    /**
     * Initializes the exception using the given {@code message}.
     *
     * @param message The message describing the exception
     */
    public MessagingConfigurationException(String message) {
        super(message);
    }

    /**
     * Initializes the exception using the given {@code message} and {@code cause}.
     *
     * @param message The message describing the exception
     * @param cause   The underlying cause of the exception
     */
    public MessagingConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
