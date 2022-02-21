package io.iamcyw.tower.exception;

public class MessageIllegalException extends RuntimeException {
    private static final long serialVersionUID = -3807222391429108353L;

    /**
     * 系统运行时异常
     *
     * @param s 异常消息
     */
    public MessageIllegalException(ErrorMessage s) {
        super(s.toString());
    }

    /**
     * 系统运行时异常
     *
     * @param s     异常消息
     * @param cause 故障问题
     */
    public MessageIllegalException(ErrorMessage s, Throwable cause) {
        super(s.toString(), cause);
    }

}
