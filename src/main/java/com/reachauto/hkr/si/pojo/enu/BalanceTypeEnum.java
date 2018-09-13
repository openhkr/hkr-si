package com.reachauto.hkr.si.pojo.enu;

/**
 * Created by Administrator on 2018/2/8.
 */
public enum BalanceTypeEnum {

    COMMON_BALANCE(1, "普通余额"),
    DEPOSIT_BALANCE(2, "保证金余额");

    private final Integer code;
    private final String name;

    BalanceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }
}