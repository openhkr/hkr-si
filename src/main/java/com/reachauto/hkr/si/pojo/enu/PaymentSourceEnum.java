package com.reachauto.hkr.si.pojo.enu;

import com.reachauto.hkr.si.service.impl.*;

/**
 * Created by Administrator on 2018/1/15.
 */
public enum PaymentSourceEnum {
    COMMON_BALANCE(1, "普通余额", BalancePayServiceImpl.class, BalanceQueryServiceImpl.class, BalanceRefundServiceImpl.class),
    ALI(2, "支付宝", AliPayServiceImpl.class, AliPayQueryServiceImpl.class, AliRefundServiceImpl.class),
    WECHAT(4, "微信支付", WeChatPayServiceImpl.class, WeChatPayQueryServiceImpl.class, WeChatRefundServiceImpl.class),
    DEPOSIT_BALANCE(16, "保证金余额", BalancePayServiceImpl.class, BalanceQueryServiceImpl.class, BalanceRefundServiceImpl.class);

    private final int code;
    private final String name;
    private final Class payClazz;
    private final Class queryClazz;
    private final Class refundClazz;

    PaymentSourceEnum(int code, String name, Class payClazz, Class queryClazz, Class refundClazz) {
        this.code = code;
        this.name = name;
        this.payClazz = payClazz;
        this.queryClazz = queryClazz;
        this.refundClazz = refundClazz;
    }

    public int getCode() {
        return code;
    }

    /**
     * 转换成余额类型
     * @return
     */
    public int getBalanceType(){
        if(code == COMMON_BALANCE.code){
            return BalanceTypeEnum.COMMON_BALANCE.getCode();
        }
        if(code == DEPOSIT_BALANCE.code){
            return BalanceTypeEnum.DEPOSIT_BALANCE.getCode();
        }
        return 0;
    }

    public Class getPayClazz() {
        return payClazz;
    }

    public Class getQueryClazz() {
        return queryClazz;
    }

    public Class getRefundClazz(){
        return refundClazz;
    }

    public static PaymentSourceEnum getType(int code) {
        for (PaymentSourceEnum status : PaymentSourceEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
