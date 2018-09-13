package com.reachauto.hkr.exception;

/**
 * Created with IntelliJ IDEA.
 * User: chenxiangning
 * Date: 2017-12-28 10:58
 * This is my work in reachauto code.
 * mail:chenxiangning@reachauto.com
 * Description: 全局应答码
 */
public class GlobalExceptionCode {

    /**
     * B 未登录或无权限：2，未登录、x-auth-token过期、该用户无访问当前接口的权限都会返回该应答码
     */
    public static final int NOT_LOGGED_IN_OR_NO_PERMISSIONS = 2;
    public static final String NOT_LOGGED_IN_OR_NO_PERMISSIONS_MSG = "未登录或无权限";

    /**
     * C 未查询到数据：5，查询单条或多条信息的场合，没有查询到任何结果返回该应答码
     */
    public static final int NO_DATA_QUERIED = 5;
    public static final String NO_DATA_QUERIED_MSG = "未查询到数据";

    /**
     * D 未知异常：3000，当服务端发生未知异常的场合，返回该应答码
     */
    public static final int UNKNOWN_EXCEPTION = 3000;
    public static final String UNKNOWN_EXCEPTION_MSG = "未知异常";

    /**
     * E 调用端异常：4000，当客户端或者微服务之间调用的入参等原因引起的异常时，会返回4000-4999之间的应答码，例如：参数异常会返回4010
     */
    public static final int APP_SIDE_EXCEPTION = 4000;
    public static final String APP_SIDE_EXCEPTION_MSG = "调用端异常";

    /**
     * 参数异常会返回4010
     */
    public static final int APP_SIDE_PARAMETER_EXCEPTION = 4010;
    public static final String APP_SIDE_PARAMETER_EXCEPTION_MSG = "请求参数异常";

    /**
     * JSON格式错误
     */
    public static final int APP_SIDE_JSON_FORMAT_EXCEPTION = 4011;
    public static final String APP_SIDE_JSON_FORMAT_EXCEPTION_MSG = "JSON格式错误";

    /**
     * F 服务端异常：5000，当服务端内部处理发生异常的场合，返回5000-5010之间的应答码，例如：业务异常的默认应答码为5010
     */
    public static final int SERVER_SIDE_EXCEPTIONS = 5000;
    public static final String SERVER_SIDE_EXCEPTIONS_MSG = "服务端异常";

    /**
     * 业务异常的默认应答码为5010
     */
    public static final int SERVER_SIDE_DEFAULT_EXCEPTIONS = 5010;
    public static final String SERVER_SIDE_DEFAULT_EXCEPTIONS_MSG = "业务代码异常抛异常了";


}

























