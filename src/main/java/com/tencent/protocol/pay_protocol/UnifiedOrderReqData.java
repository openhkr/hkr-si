package com.tencent.protocol.pay_protocol;

import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.protocol.AppInfo;
import com.tencent.protocol.WechatPayReqData;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一下单请求数据格式
 *
 * @author 刘宇
 */
public class UnifiedOrderReqData implements WechatPayReqData {

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
    private int total_fee;           //总金额
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
    ) {
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));//随机字符串，不长于32 位

        setBody(title);
        //setDetail(detail);//商品名称明细列表
        //setAttach(attach); //支付订单里面可以填的附加数据，API会将提交的这个附加数据原样返回，有助于商户自己可以注明该笔消费的具体内容，方便后续的运营和记录
        setOut_trade_no(out_trade_no);//商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一,app获取后查询支付金额
        setFee_type("CNY");//货币类型，默认人民币：CNY
        setTotal_fee(payment.multiply(new BigDecimal(100)).intValue());//订单总金额，单位为“分”，进行单位处理
        setSpbill_create_ip("0.0.0.0");//订单生成的机器IP，用户端实际IP
        //setGoods_tag(goods_tag);//商品标记，微信平台配置的商品标记，用于优惠券或者满减使用
        setNotify_url(notifyUrl);//接收微信支付异步通知回调地址,后续默认写死，暂时为空
        setOpenid(openId);

        String signstr;

        setAppid(appInfo.getAppID());//应用ID
        setMch_id(appInfo.getMchID());//商户号
        // 交易类型
        setTrade_type(appInfo.getTradeType());
        signstr = Signature.getSign(toMap(), appInfo.getKey());//根据API给的签名规则进行签名

        setSign(signstr);//把签名数据设置到Sign这个属性中
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getLimit_pay() {
        return limit_pay;
    }

    public void setLimit_pay(String limit_pay) {
        this.limit_pay = limit_pay;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
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
