package com.tencent.protocol.refund_protocol;

import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.protocol.AppInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 申请退款请求数据格式
 *
 * @author 刘宇
 */
public class RefundReqData {

    //每个字段具体的意思请查看API文档
    private String appid = "";
    private String mch_id = "";
    private String device_info = "";
    private String nonce_str = "";
    private String sign = "";
    private String transaction_id = "";
    private String out_trade_no = "";
    private String out_refund_no = "";
    private int total_fee = 0;
    private int refund_fee = 0;
    private String refund_fee_type = "CNY";
    private String op_user_id = "";
    private String refund_account = null;

    /**
     * 请求退款服务
     *
     * @param transactionID 是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。建议优先使用
     * @param outTradeNo    商户系统内部的订单号,transaction_id 、out_trade_no 二选一，如果同时存在优先级：transaction_id>out_trade_no
     * @param deviceInfo    微信支付分配的终端设备号，与下单一致
     * @param outRefundNo   商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * @param totalFee      订单总金额，单位为分
     * @param refundFee     退款总金额，单位为分,可以做部分退款
     * @param opUserID      操作员帐号, 默认为商户号
     * @param refundFeeType 货币类型，符合ISO 4217标准的三位字母代码，默认为CNY（人民币）
     * @param refundAccount 退款资金来源 true UNSETTLED未结算资金退款 false RECHARGE可用余额退款
     */
    public RefundReqData(AppInfo appInfo, String transactionID, String outTradeNo, String deviceInfo, Long outRefundNo, int totalFee, int refundFee, String opUserID, String refundFeeType, boolean refundAccount) {

        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(appInfo.getAppID());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(appInfo.getMchID());

        //transaction_id是微信系统为每一笔支付交易分配的订单号，通过这个订单号可以标识这笔交易，它由支付订单API支付成功时返回的数据里面获取到。
        setTransaction_id(transactionID);

        //商户系统自己生成的唯一的订单号
        setOut_trade_no(outTradeNo);

        //微信支付分配的终端设备号，与下单一致
        setDevice_info(deviceInfo);

        setOut_refund_no(String.valueOf(outRefundNo));

        setTotal_fee(totalFee);

        setRefund_fee(refundFee);

        setOp_user_id(opUserID);

        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));

        if (refundAccount) {
            refund_account = "REFUND_SOURCE_UNSETTLED_FUNDS";
        } else {
            refund_account = "REFUND_SOURCE_RECHARGE_FUNDS";
        }

        //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap(), appInfo.getKey());
        setSign(sign);//把签名数据设置到Sign这个属性中
    }

    public void setRefundAccountUnsettled() {
        this.refund_account = "REFUND_SOURCE_UNSETTLED_FUNDS";
    }

    public void setRefundAccountRecharge() {
        this.refund_account = "REFUND_SOURCE_RECHARGE_FUNDS";
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

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
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

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public int getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(int refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getOp_user_id() {
        return op_user_id;
    }

    public void setOp_user_id(String op_user_id) {
        this.op_user_id = op_user_id;
    }

    public String getRefund_fee_type() {
        return refund_fee_type;
    }

    public void setRefund_fee_type(String refund_fee_type) {
        this.refund_fee_type = refund_fee_type;
    }

    public String getRefund_account() {
        return refund_account;
    }

    public void setRefund_account(String refund_account) {
        this.refund_account = refund_account;
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

    @Override
    public String toString() {
        return "RefundReqData{" +
                "appid='" + appid + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", device_info='" + device_info + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", out_refund_no='" + out_refund_no + '\'' +
                ", total_fee=" + total_fee +
                ", refund_fee=" + refund_fee +
                ", refund_fee_type='" + refund_fee_type + '\'' +
                ", op_user_id='" + op_user_id + '\'' +
                ", refund_account='" + refund_account + '\'' +
                '}';
    }
}
