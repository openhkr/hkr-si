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
public class WechatRefundResult {

    /**
     * 微信文档：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2
     */
    private String return_code = "";
    private String return_msg = "";
    private String result_code = "";
    private String err_code = "";
    private String err_code_des = "";
    
    private String appid = "";
    private String mch_id = "";
    private String nonce_str = "";
    private String sign = "";
    private String transaction_id = "";
    private String out_trade_no = "";
    private String out_refund_no = "";
    private String refund_id = "";
    private String refund_fee = "";
    private String settlement_refund_fee = "";
    private String total_fee = "";
    private String settlement_total_fee = "";
    private String fee_type = "";
    private String cash_fee = "";
    private String coupon_refund_fee = "";
}
