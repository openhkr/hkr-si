package com.reachauto.hkr.si.pojo.parameter;

import com.reachauto.hkr.si.utils.GsonTool;
import com.reachauto.hkr.si.utils.wechat.Signature;
import com.tencent.common.RandomStringGenerator;
import com.tencent.protocol.AppInfo;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 统一下单请求数据格式
 * @author 刘宇
 */
@Data
@XStreamAlias("xml")
public class UnifiedOrderReqData {

    private String appid;               //应用ID
    private String mch_id;              //商户号
    private String device_info;         //设备号   可为空
    private String nonce_str;           //随机字符串,随机字符串，不长于32位
    private String sign;                //签名
    private String body;                //商品描述
    private String detail;              //商品详情  可为空
    private String attach;              //附加数据  可为空
    private String out_trade_no;        //商户订单号
    private String fee_type;            //货币类型  可为空
    private String total_fee;           //总金额
    private String spbill_create_ip;    //终端IP
    private String time_start;          //交易起始时间  可为空
    private String time_expire;         //交易结束时间  可为空
    private String goods_tag;           //商品标记，代金券或立减优惠功能的参数  可为空
    private String notify_url;          //通知地址,接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
    private String trade_type;          //交易类型
    private String limit_pay;           //指定支付方式 ,no_credit--指定不能使用信用卡支付  可为空
    private String openid;              //微信公众号支付的时候,需要

    /**
     * 设置统一下单数据(后续微信支付与支付宝整合，还会改动，测试数据写死)
     *
     * @param out_trade_no 相当于系统的订单号
     * @param payment      支付金额(处理后)
     */
    public UnifiedOrderReqData(
            AppInfo appInfo,
            String title,
            String out_trade_no,
            BigDecimal payment,
            String notifyUrl,
            String openId
    )
    {
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));//随机字符串，不长于32 位

        setBody(title);
        //setDetail(detail);//商品名称明细列表
        //setAttach(attach); //支付订单里面可以填的附加数据，API会将提交的这个附加数据原样返回，有助于商户自己可以注明该笔消费的具体内容，方便后续的运营和记录
        setOut_trade_no(out_trade_no);//商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一,app获取后查询支付金额
        setFee_type("CNY");//货币类型，默认人民币：CNY
        setTotal_fee(String.valueOf(payment.multiply(BigDecimal.valueOf(100)).intValue()));//订单总金额，单位为“分”，进行单位处理
        setSpbill_create_ip("0.0.0.0");//订单生成的机器IP，用户端实际IP
        //setGoods_tag(goods_tag);//商品标记，微信平台配置的商品标记，用于优惠券或者满减使用
        setNotify_url(notifyUrl);//接收微信支付异步通知回调地址,后续默认写死，暂时为空
        setOpenid(openId);

        String signstr;

        setAppid(appInfo.getAppID());//应用ID
        setMch_id(appInfo.getMchID());//商户号
        // 交易类型
        setTrade_type(appInfo.getTradeType());

        // 签名之前sign参数必须为空，此时它不参与签名
        String json = GsonTool.objectToAllFieldNullJson(this);
        this.sign = Signature.getSign(GsonTool.jsonToMap(json), appInfo.getKey());
    }
}
