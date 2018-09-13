package com.reachauto.hkr.si.pojo.parameter;

import com.reachauto.hkr.si.utils.GsonTool;
import com.reachauto.hkr.si.utils.wechat.Signature;
import com.tencent.common.RandomStringGenerator;
import com.tencent.protocol.AppInfo;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 查询订单请求数据格式(后续要改动)
 *
 * @author 刘宇
 */
@Data
@XStreamAlias("xml")
public class WechatPayQueryReqData {

    /**
     * 每个字段具体的意思请查看API文档
     */
    private String appid = "";
    private String mch_id = "";
    private String transaction_id = "";
    private String out_trade_no = "";
    private String nonce_str = "";
    private String sign = "";

    /**
     * 支付查询服务(优先使用财付通账号)
     *
     * @param transactionID 是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。建议优先使用
     * @param outTradeNo    商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @return API返回的XML数据
     * @throws Exception
     */
    public WechatPayQueryReqData(AppInfo appInfo, String transactionID, String outTradeNo) {

        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(appInfo.getAppID());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(appInfo.getMchID());

        //transaction_id是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。
        setTransaction_id(transactionID);

        //商户系统自己生成的唯一的订单号
        setOut_trade_no(outTradeNo);

        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));

        String json = GsonTool.objectToAllFieldEmptyJson(this);
        // 签名之前sign参数必须为空，此时它不参与签名
        this.sign = Signature.getSign(GsonTool.jsonToMap(json), appInfo.getKey());
    }
}
