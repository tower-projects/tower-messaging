package io.iamcyw.tower.exception;

import io.iamcyw.tower.utils.lang.StringPool;
import org.apache.commons.lang3.StringUtils;

public class ErrorMessage {

    private String error;

    private String errorMsg;

    private Object[] args;

    public ErrorMessage(String error, String errorMsg, Object... args) {
        this.error = error;
        this.errorMsg = errorMsg;
        this.args = args;
    }

    public ErrorMessage(String error, String errorMsg) {
        this.error = error;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return error + " -- " + arrayFormat(errorMsg, args);
    }

    private String arrayFormat(String messagePattern, Object[] argArray) {
        if (argArray != null && argArray.length > 0) {
            for (Object arg : argArray) {
                messagePattern = StringUtils.replaceOnce(messagePattern, StringPool.LEFT_BRACE + StringPool.RIGHT_BRACE,
                                                         String.valueOf(arg));
            }
        }
        return messagePattern;
    }

}
