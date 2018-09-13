package com.reachauto.hkr.exception;

/**
 * Created with IntelliJ IDEA.
 * User: chenxiangning
 * Date: 2017-12-28 11:27
 * This is my work in reachauto code.
 * mail:chenxiangning@reachauto.com
 * Description: 我们项目中的异常老大
 */
public abstract class HkrRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 6362399925738747938L;

    // 异常标示码
    private int code;

    // 异常集合
    private String description;

    /**
     * 构建一个异常传递消息描述
     *
     * @param code
     */
    public HkrRuntimeException(int code) {
        super();
        this.code = code;
    }

    /**
     * 构建一个异常传递消息描述
     *
     * @param msg
     */
    public HkrRuntimeException(String msg) {
        super(msg);
    }

    /**
     * 构建一个异常
     *
     * @param code
     * @param description
     */
    public HkrRuntimeException(int code, String description) {
        super(description);
        this.code = code;
        this.description = description;
    }

    /**
     * 构建一个异常
     *
     * @param code
     * @param description
     */
    public HkrRuntimeException(int code, String description, String msg) {
        super(msg);
        this.code = code;
        this.description = description;
    }

    /**
     * 构建一个异常传递消息描述,cause
     *
     * @param msg
     * @param cause
     */
    public HkrRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param code
     * @param description
     * @param cause
     */
    public HkrRuntimeException(int code, String description, Throwable cause) {
        super(description,cause);
        this.code = code;
        this.description = description;
    }

    /**
     * 返回消息
     *
     * @return
     */
    @Override
    public String getMessage() {
        return buildMessage(super.getMessage(), null);
    }

    /**
     * 返回消息,如果cause嵌套里面有值,会加上
     *
     * @return
     */
    public String getMessageCause() {
        return buildMessage(super.getMessage(), getCause());
    }

    /**
     * 获取异常信息
     *
     * @return
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 异常码获取
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * 检索该异常的最内层原因，如果有返回最内层的异常，如果没有返回空
     *
     * @return
     */
    public Throwable getRootCause() {
        Throwable rootCause = null;
        Throwable cause = getCause();
        while (cause != null && cause != rootCause) {
            rootCause = cause;
            cause = cause.getCause();
        }
        return rootCause;
    }

    /**
     * 检索此异常的最具体原因
     * Differs from {@link #getRootCause()} in that it falls back
     *
     * @return
     */
    public Throwable getMostSpecificCause() {
        Throwable rootCause = getRootCause();
        return (rootCause != null ? rootCause : this);
    }

    /**
     * 检查该异常是否包含给定类型的异常:
     * 它是给定的类本身，或它包含一个嵌套的异常
     *
     * @param exType 异常类型查找
     * @return 是否有指定类型的嵌套异常
     */
    public boolean contains(Class<?> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        Throwable cause = getCause();
        if (cause == this) {
            return false;
        }
        if (cause instanceof HkrRuntimeException) {
            return ((HkrRuntimeException) cause).contains(exType);
        } else {
            while (cause != null) {
                if (exType.isInstance(cause)) {
                    return true;
                }
                if (cause.getCause() == cause) {
                    break;
                }
                cause = cause.getCause();
            }
            return false;
        }
    }

    /**
     * 为给定的消息和根原因构建一个消息
     *
     * @param message 消息
     * @param cause   根原因
     * @return 返回异常消息
     */
    private String buildMessage(String message, Throwable cause) {
        if (cause != null) {
            StringBuilder sb = new StringBuilder();
            if (message != null) {
                sb.append(message).append("; ");
            }
            sb.append("nested exception is ").append(cause);
            return sb.toString();
        } else {
            return message;
        }
    }

}