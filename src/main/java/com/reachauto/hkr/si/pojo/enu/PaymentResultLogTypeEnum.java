package com.reachauto.hkr.si.pojo.enu;

/**
 * Created by Administrator on 2018/1/15.
 */
public enum PaymentResultLogTypeEnum {
    QUERY(1, "查询"),
    CALLBACK(2, "回调"),
    REFUND(4, "退款");

    private final int code;
    private final String name;

    PaymentResultLogTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }


    public static PaymentResultLogTypeEnum getType(int code) {
        for (PaymentResultLogTypeEnum status : PaymentResultLogTypeEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
