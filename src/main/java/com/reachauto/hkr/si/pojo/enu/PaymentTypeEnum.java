package com.reachauto.hkr.si.pojo.enu;

/**
 * @author zhangshuo
 */
public enum PaymentTypeEnum {
    PAYMENT(2, "支付订单"),
    RECHARGE(1, "充值订单");

    private final int code;
    private final String name;

    PaymentTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PaymentTypeEnum getType(int code) {
        for (PaymentTypeEnum status : PaymentTypeEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
