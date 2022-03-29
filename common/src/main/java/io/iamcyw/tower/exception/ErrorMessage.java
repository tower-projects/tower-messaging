package io.iamcyw.tower.exception;

import com.google.common.base.Strings;
import io.iamcyw.tower.utils.CommonKit;

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
        if (Strings.isNullOrEmpty(error)) {
            return arrayFormat(errorMsg, args);
        }
        return error + " -- " + arrayFormat(errorMsg, args);
    }

    private String arrayFormat(String messagePattern, Object[] argArray) {
        if (argArray != null && argArray.length > 0) {
            return CommonKit.arrayFormat(messagePattern, argArray);
        }
        return messagePattern;
    }

}
