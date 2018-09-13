package com.reachauto.hkr.si.constant;

/**
 * @author zhangshuo
 * @date 2018-01-16
 */
public final class WeChatConstant {

    public static final String WECHAT_PAY_NAME = "APP";

    public static final String JSAPI_NAME = "JSAPI";

    public static final String WECHAT_SUCCESS = "SUCCESS";
    public static final String WECHAT_FAIL = "FAIL";
    public static final String WECHAT_REFUND = "REFUND";
    public static final String WECHAT_CLOSED = "CLOSED";
    public static final String WECHAT_REVOKED = "REVOKED";
    public static final String WECHAT_PAYERROR = "PAYERROR";
    public static final String WECHAT_ORDERNOTEXIST = "ORDERNOTEXIST";

    /**
     * 订单已支付，不能发起关单，请当作已支付的正常交易
     */
    public static final String WECHAT_ORDERPAID = "ORDERPAID";

    /**
     * 订单已关闭，无需继续调用
     */
    public static final String WECHAT_ORDERCLOSED = "ORDERCLOSED";

    private WeChatConstant() {
    }
}
