package io.iamcyw.tower.exception;

public class MessageIllegalStateException extends MessageIllegalException {
    public MessageIllegalStateException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

}
