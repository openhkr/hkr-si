package com.reachauto.hkr.si.pojo.result;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangshuo
 */
@Data
@Slf4j
@XStreamAlias("xml")
public class WechatPayQueryResult {

    /**
     * 微信查询接口文档：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2
     */
    @XStreamAlias("return_code")
    private String returnCode;

    @XStreamAlias("return_msg")
    private String returnMsg;

    @XStreamAlias("appid")
    private String appid;

    @XStreamAlias("mch_id")
    private String mchId;

    @XStreamAlias("device_info")
    private String deviceInfo;

    @XStreamAlias("nonce_str")
    private String nonceStr;

    @XStreamAlias("sign")
    private String sign;

    @XStreamAlias("result_code")
    private String resultCode;

    @XStreamAlias("openid")
    private String openid;

    @XStreamAlias("is_subscribe")
    private String isSubscribe;

    @XStreamAlias("trade_type")
    private String tradeType;

    @XStreamAlias("bank_type")
    private String bankType;

    @XStreamAlias("total_fee")
    private String totalFee;

    @XStreamAlias("fee_type")
    private String feeType;

    @XStreamAlias("transaction_id")
    private String transactionId;

    @XStreamAlias("out_trade_no")
    private String outTradeNo;

    @XStreamAlias("attach")
    private String attach;

    @XStreamAlias("time_end")
    private String timeEnd;

    @XStreamAlias("trade_state")
    private String tradeState;

    @XStreamAlias("err_code")
    private String errCode;
}
