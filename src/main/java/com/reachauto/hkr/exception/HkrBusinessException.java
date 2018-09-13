package com.reachauto.hkr.exception;


/**
 * Created with IntelliJ IDEA.
 * User: chenxiangning
 * Date: 2017-12-28 11:27
 * This is my work in reachauto code.
 * mail:chenxiangning@reachauto.com
 * Description: 开发中业务异常,请使用它
 */
public class HkrBusinessException extends HkrServerException {


    public HkrBusinessException() {
        super(GlobalExceptionCode.SERVER_SIDE_DEFAULT_EXCEPTIONS, GlobalExceptionCode.SERVER_SIDE_DEFAULT_EXCEPTIONS_MSG);
    }

    public HkrBusinessException(int code) {
        super(code);
    }

    public HkrBusinessException(String description) {
        super(GlobalExceptionCode.SERVER_SIDE_DEFAULT_EXCEPTIONS, description);
    }

    public HkrBusinessException(int code, String description) {
        super(code, description);
    }

    public HkrBusinessException(String description, Throwable cause) {
        super(description, cause);
    }


    public HkrBusinessException(int code, String description, Throwable cause) {
        super(code, description, cause);
    }

}
