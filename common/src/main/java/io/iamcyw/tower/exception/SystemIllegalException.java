package io.iamcyw.tower.exception;

import io.iamcyw.tower.utils.lang.NonNull;

public class SystemIllegalException extends RuntimeException {
    private static final long serialVersionUID = -3807222391429108353L;

    /**
     * exception code 异常代码
     */
    @NonNull
    private int code;

    public SystemIllegalException(String s) {
        super(s);
        this.code = ErrorCode.UNKNOW;
    }

    /**
     * 系统运行时异常
     *
     * @param s     异常消息
     * @param cause 故障问题
     */
    public SystemIllegalException(String s, Throwable cause) {
        super(s, cause);
        this.code = ErrorCode.UNKNOW;
    }

    /**
     * 系统运行时异常
     *
     * @param code 异常代码
     * @param s    异常消息
     */
    public SystemIllegalException(int code, String s) {
        super(s);
        this.code = code;
    }

    /**
     * 系统运行时异常
     *
     * @param code  异常代码
     * @param s     异常消息
     * @param cause 故障问题
     */
    public SystemIllegalException(int code, String s, Throwable cause) {
        super(s, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
