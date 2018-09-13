package com.reachauto.hkr.si.pojo.bo;

import lombok.Data;

import java.util.Objects;

/**
 * @author zhangshuo
 * 内部公共返回，成功，失败，超时
 * 可以有返回体
 */
@Data
public class InnerResultBO<T> {

    private static final int SUCCESS = 1;
    private static final int FAIL = 2;
    private static final int TIME_OUT = 4;

    private Integer status;
    private T t;

    public InnerResultBO(int status){
        this.status = status;
    }

    public InnerResultBO(int status, T t){
        this.t = t;
    }

    public static InnerResultBO getTimeOutInstants() {
        return new InnerResultBO(TIME_OUT);
    }

    public InnerResultBO setToTimeOut() {
        this.status = TIME_OUT;
        return this;
    }

    public static InnerResultBO getSuccessInstants() {
        return new InnerResultBO(SUCCESS);
    }

    public static InnerResultBO getFailInstants() {
        return new InnerResultBO(FAIL);
    }

    public boolean isSuccessed() {
        return Objects.equals(status, new Integer(SUCCESS));
    }

    public boolean isFailed() {
        return Objects.equals(status, new Integer(FAIL));
    }

    public boolean isTimeOut() {
        return Objects.equals(status, new Integer(TIME_OUT));
    }
}
