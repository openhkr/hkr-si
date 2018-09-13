package com.reachauto.hkr.si.pojo.enu;

/**
 * Created by Administrator on 2018/1/17.
 */
public enum TradeStatusEnum {
    TRADE_SUCCESS(1, "处理成功"),
    TRADE_FAIL(2, "处理失败"),
    TRADE_PROCESSING(4, "处理中"),
    TRADE_SETTLEMENT(8, "结算中"),
    TRADE_REFUNDING(16, "退款中"),
    TRADE_REFUNDED(32, "退款成功"),
    TRADE_MANUAL_REFUNDING(64, "财务退款中"),
    TRADE_MANUAL_REFUNDED(128, "财务退款成功");

    private final Integer code;
    private final String name;

    TradeStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }
}
