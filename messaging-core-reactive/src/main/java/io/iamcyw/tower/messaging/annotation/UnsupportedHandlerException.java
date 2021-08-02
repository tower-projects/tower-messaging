package io.iamcyw.tower.messaging.annotation;


import io.iamcyw.tower.common.MessagingConfigurationException;

import java.lang.reflect.Member;

/**
 * Thrown when an @...Handler annotated method was found that does not conform to the rules that apply to those
 * methods.
 */
public class UnsupportedHandlerException extends MessagingConfigurationException {

    private static final long serialVersionUID = 7991150193173243668L;

    private final Member violatingMethod;

    /**
     * Initialize the exception with a {@code message} and the {@code violatingMethod}.
     *
     * @param message         a descriptive message of the violation
     * @param violatingMethod the method that violates the rules of annotated Event Handlers
     */
    public UnsupportedHandlerException(String message, Member violatingMethod) {
        super(message);
        this.violatingMethod = violatingMethod;
    }

    /**
     * A reference to the method that violated the event handler rules.
     *
     * @return the method that violated the event handler rules
     */
    public Member getViolatingMethod() {
        return violatingMethod;
    }

}
