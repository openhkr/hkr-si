package com.reachauto.hkr.si.constant;

/**
 * Created by Administrator on 2018/1/15.
 */
public final class AliPayConstants {

    //允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
    //注：若为空，则默认为15d
    public static final String TIME_OUT_EXPRESS = "15d";

    //销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
    public static final String PRODUCT_CODE = "QUICK_MSECURITY_PAY";

    //商品主类型：0—虚拟类商品，1—实物类商品
    //注：虚拟类商品不支持使用花呗渠道
    public static final String GOOD_TYPE = "1";

    public static final String ALI_SUCCESS_CODE = "10000";
    public static final String ALI_BIZ_ERR_CODE = "40004";
    public static final String ALI_BIZ_ERR_SUB_CODE_TRADE_NOT_EXIST = "ACQ.TRADE_NOT_EXIST";

    //支付宝支付查询返回支付状态
    //交易创建，等待买家付款
    public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    //未付款交易超时关闭，或支付完成后全额退款
    public static final String TRADE_CLOSED = "TRADE_CLOSED";
    //交易支付成功
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    //交易结束，不可退款
    public static final String TRADE_FINISHED = "TRADE_FINISHED";


    private AliPayConstants() {
        //构造函数私有化
    }
}
