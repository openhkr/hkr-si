package com.reachauto.hkr.si.pojo.parameter;

import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.protocol.AppInfo;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 申请退款请求数据格式
 * @author zhangshuo
 */
@Data
@XStreamAlias("xml")
public class WechatRefundParameter{

    /**
     * 每个字段具体的意思请查看API文档
     * https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_4&index=6
     * 由于签名需要，此处字段不能采用驼峰
     */
    private String appid = "";

    private String mch_id = "";

    private String nonce_str = "";

    private String sign = "";

    private String transaction_id = "";

    private String out_trade_no = "";

    private String out_refund_no = "";

    private int total_fee = 0;

    private int refund_fee = 0;

    /**
     * 货币类型，符合ISO 4217标准的三位字母代码，默认为CNY（人民币）
     */
    private String refund_fee_type = "CNY";

    private String refund_desc = "";
    /**
     * 退款资金来源 true UNSETTLED未结算资金退款 false RECHARGE可用余额退款
     */
    private String refund_account = "REFUND_SOURCE_UNSETTLED_FUNDS";

    /**
     * 请求退款服务
     * @param outTradeNo    商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @param outRefundNo   商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * @param totalFee      订单总金额，单位为分
     * @param refundFee     退款总金额，单位为分,可以做部分退款
     */
    public WechatRefundParameter(AppInfo appInfo, String outTradeNo, String transactionID, String outRefundNo, int totalFee, int refundFee, String refund_desc) {

        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(appInfo.getAppID());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(appInfo.getMchID());

        //transaction_id是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。
        setTransaction_id(transactionID);

        //商户系统自己生成的唯一的订单号
        //setOut_trade_no(outTradeNo);

        setOut_refund_no(outRefundNo);

        setTotal_fee(totalFee);

        setRefund_fee(refundFee);

        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));

        setRefund_desc(refund_desc);

        //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap(), appInfo.getKey());
        //把签名数据设置到Sign这个属性中
        setSign(sign);
    }

    /**
     * 生成微信用以签名的MAP
     * @return
     */
    protected Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if (obj != null) {
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
