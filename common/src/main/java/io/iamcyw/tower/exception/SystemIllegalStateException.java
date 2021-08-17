package io.iamcyw.tower.exception;

public class SystemIllegalStateException extends SystemIllegalException {
    public SystemIllegalStateException(String s) {
        super(ErrorCode.STATE, s);
    }

}
