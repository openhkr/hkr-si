package com.reachauto.hkr.si;

/**
 * Created by Administrator on 2017/6/15.
 */
public final class ErrCodeConstant {

    //订单结算中
    public static final int ORDER_SETTLEMENT = 14001;
    //支付已完成
    public static final int ORDER_FINISHED = 14002;
    //无当前流水记录
    public static final int NO_RECORD = 14003;
    //用户不存在
    public static final int NO_SUCH_MEMBER = 14004;
    //不支持该支付方式
    public static final int NO_SUCH_PAYMENT_TYPE = 14005;
    //下单签名失败
    public static final int PAY_ERROR = 14006;
    //余额不足
    public static final int BALANCE_NOT_ENOUGH = 14007;

    // 订单不存在
    public static final int ORDER_NOTEX = 14041;
    // 订单已退款
    public static final int ORDER_HAS_REF = 14042;
    // 退款金额错误
    public static final int AMOUNT_ERROR = 14044;
    // 未支付成功的订单不可退款
    public static final int ORDER_CANNOT_REFUND = 14045;
    // 退款进行中
    public static final int ORDER_REFUNDING = 14046;
    // 退款失败
    public static final int REFUND_FAILED = 14047;
    // 订单类型不正确
    public static final int ORDER_TYPE_ERROR = 14048;
    // 订单已经超过退款时间
    public static final int ORDER_REFUND_TIMEOUT = 14049;

    private ErrCodeConstant() {
        //构造函数私有化
    }
}
