package com.reachauto.hkr.exception;


/**
 * Created with IntelliJ IDEA.
 * User: chenxiangning
 * Date: 2017-12-28 11:27
 * This is my work in reachauto code.
 * mail:chenxiangning@reachauto.com
 * Description: 服务端引发的异常可以使用
 */
public class HkrServerException extends HkrRuntimeException {

    private static final long serialVersionUID = -256755294469277125L;

    public HkrServerException() {
        super(GlobalExceptionCode.SERVER_SIDE_EXCEPTIONS, String.format("[%s]", GlobalExceptionCode.SERVER_SIDE_EXCEPTIONS_MSG));
    }

    public HkrServerException(int code) {
        super(code, String.format("[%s]", GlobalExceptionCode.SERVER_SIDE_EXCEPTIONS_MSG));
    }

    public HkrServerException(String description) {
        super(GlobalExceptionCode.SERVER_SIDE_EXCEPTIONS, String.format("%s", description));

    }

    public HkrServerException(int code, String description) {
        super(code, String.format("%s", description));
    }

    public HkrServerException(String description, Throwable cause) {
        super(String.format("%s", description), cause);
    }


    public HkrServerException(int code, String description, Throwable cause) {
        super(code, String.format("%s", description), cause);
    }

}
