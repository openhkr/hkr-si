package com.reachauto.hkr.si.pojo.enu;

/**
 * Created by Administrator on 2017/3/14.
 */
public enum OrderTypeEnum {
    OTHERS(0, "其它未识别"),
    RENTAL(1, "租车"),
    CHARGING(2, "充电"),
    SELF_PAYMENT(4, "自助缴费"),
    DEPOSIT(8, "租车押金"),
    RECHARGE(16, "充值"),
    RESERVATION_BOOKING(32, "预约租车预约费"),
    RESERVATION_BILLING(64, "预约租车计价费"),
    DEPOSIT_DEDUCT(128, "租车押金抵扣");

    private final int code;
    private final String name;

    OrderTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 转换成余额类型，用于充值和充值退款时根据订单类型找到对应的余额类型。
     * @return
     */
    public int getBalanceType(){
        if(code == RECHARGE.code){
            return BalanceTypeEnum.COMMON_BALANCE.getCode();
        }
        if(code == DEPOSIT.code){
            return BalanceTypeEnum.DEPOSIT_BALANCE.getCode();
        }
        return 0;
    }

    public static OrderTypeEnum getType(int code) {
        for (OrderTypeEnum status : OrderTypeEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return OrderTypeEnum.OTHERS;
    }
}
