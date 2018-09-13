package com.reachauto.hkr.exception;

/**
 * Created with IntelliJ IDEA.
 * User: chenxiangning
 * Date: 2017-12-28 11:27
 * This is my work in reachauto code.
 * mail:chenxiangning@reachauto.com
 * Description: 请求参数异常
 */
public class TennisToolException extends HkrServerException {


    private static final long serialVersionUID = -6164729939471924058L;

    public TennisToolException() {
        super(GlobalExceptionCode.APP_SIDE_PARAMETER_EXCEPTION, GlobalExceptionCode.APP_SIDE_PARAMETER_EXCEPTION_MSG);
    }

    public TennisToolException(int code) {
        super(code);
    }

    public TennisToolException(String msg) {
        super(GlobalExceptionCode.APP_SIDE_PARAMETER_EXCEPTION, msg);
    }

    public TennisToolException(int code, String description) {
        super(code, description);
    }

    public TennisToolException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TennisToolException(int code, String description, Throwable cause) {
        super(code, description, cause);
    }

}
