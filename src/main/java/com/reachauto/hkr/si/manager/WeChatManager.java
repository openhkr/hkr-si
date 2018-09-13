package com.reachauto.hkr.si.manager;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.config.NotifyUrlProperties;
import com.reachauto.hkr.si.config.WechatConfigure;
import com.reachauto.hkr.si.constant.WeChatConstant;
import com.reachauto.hkr.si.entity.PaymentResultLogDO;
import com.reachauto.hkr.si.persistence.PaymentResultLogRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import com.reachauto.hkr.si.pojo.enu.PaymentResultLogTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.parameter.UnifiedOrderReqData;
import com.reachauto.hkr.si.pojo.parameter.WechatCloseOrderParameter;
import com.reachauto.hkr.si.pojo.parameter.WechatPayQueryReqData;
import com.reachauto.hkr.si.pojo.parameter.WechatRefundParameter;
import com.reachauto.hkr.si.pojo.result.OrderInfo;
import com.reachauto.hkr.si.pojo.result.WechatCloseOrderResult;
import com.reachauto.hkr.si.pojo.result.WechatPayQueryResult;
import com.reachauto.hkr.si.pojo.result.WechatRefundResult;
import com.reachauto.hkr.si.utils.GsonTool;
import com.reachauto.hkr.si.utils.HkrXmlUtil;
import com.reachauto.hkr.si.utils.ThreadPoolTool;
import com.reachauto.hkr.si.utils.WechatHttpClientUtil;
import com.tencent.common.Signature;
import com.tencent.common.XMLParser;
import com.tencent.protocol.AppInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * @author zhangshuo
 * @date 2018-01-15
 */
@Slf4j
@Component
@EnableConfigurationProperties(NotifyUrlProperties.class)
public class WeChatManager {

    private static final String NOMBER_100 = "100";

    @Autowired
    @Qualifier("wechatAppInfo")
    private AppInfo wechatAppInfo;

    @Autowired
    private NotifyUrlProperties notifyUrl;

    @Autowired
    private PaymentResultLogRepository paymentResultLogRepository;

    /**
     * 微信统一下单(不需要证书)
     * 交易金额单位：分
     * 公众号支付方式openId必填
     *
     * @return String 二次签名后的数据集
     */
    public String weChartUnifiedOrder(PaymentSourceEnum paymentSource, String desc, String outTradeNo, BigDecimal payment, String evnFlag, String openId) {

        log.info("微信统一下单：weChartUnifiedOrder {} {} {} {} {}", desc, outTradeNo, payment, evnFlag, openId);
        try {
            String responseStr;
            AppInfo appInfo;

            /**
             * 微信支付
             * 拼接参数，签名
             */
            appInfo = wechatAppInfo;
            UnifiedOrderReqData unifiedOrderReqData = new UnifiedOrderReqData(
                    appInfo,
                    desc,
                    outTradeNo,
                    payment,
                    notifyUrl.getWechat().concat("/").concat(evnFlag == null ? "" : evnFlag),
                    openId
            );
            String xmlParam = HkrXmlUtil.toXml(unifiedOrderReqData, UnifiedOrderReqData.class);
            responseStr = WechatHttpClientUtil.wechatPost(WechatConfigure.UNIFIEDORDER_API, xmlParam);

            log.info("统一下单返回 {}", responseStr);
            // 验签,验签失败抛出异常
            try {
                if (!Signature.checkIsSignValidFromResponseString(responseStr, appInfo.getKey())) {
                    log.error("微信统一下单错误, {}");
                    throw new HkrBusinessException();
                }
            } catch (Exception e) {
                log.error("验签过程发生异常", e);
                throw new HkrBusinessException();
            }

            /**
             * 重新签名后提供给手机端
             */
            //统一下预付单，并返回预支付号与签名信息
            String resultJsonStr = null;
            try {
                resultJsonStr = XMLParser.getUnifiedOrderResData(responseStr);
            } catch (Exception e) {
                log.error("返回数据解析错误", e);
            }
            JSONObject json = JSONObject.fromObject(resultJsonStr);
            // 这个OrderInfo构造函数里进行了签名
            OrderInfo result = new OrderInfo(json, appInfo, paymentSource);
            result.setPaymentId(result.getPaymentId());
            result.setActualPayment(String.valueOf(payment));

            // Json格式返回
            return GsonTool.objectToAllFieldNullJson(result);
        } catch (IOException e) {
            log.error("微信统一下单错误", e);
            throw new HkrBusinessException();
        }
    }

    /**
     * 查询订单状态
     *
     * @param paymentSource
     * @param outTradeNo
     * @param tradeNo
     * @return
     */
    public PaymentQueryBO weChatOrderQuery(PaymentSourceEnum paymentSource, String outTradeNo, String tradeNo) {
        try {
            String responseStr = null;
            AppInfo appInfo = null;
            // 微信支付
            appInfo = wechatAppInfo;
            WechatPayQueryReqData wechatPayQueryReqData = new WechatPayQueryReqData(
                    appInfo,
                    tradeNo,
                    outTradeNo
            );
            String xmlParam = HkrXmlUtil.toXml(wechatPayQueryReqData, WechatPayQueryReqData.class);
            responseStr = WechatHttpClientUtil.wechatPost(WechatConfigure.PAY_QUERY_API, xmlParam);

            log.info("查询订单,响应数据:{}", responseStr);

            // 验签,验签失败抛出异常
            try {
                if (!Signature.checkIsSignValidFromResponseString(responseStr, appInfo.getKey())) {
                    log.error("查询支付结果错误, {}");
                    throw new HkrBusinessException();
                }
            } catch (Exception e) {
                log.error("验签过程发生异常", e);
                throw new HkrBusinessException();
            }

            // 返回XMl转换成实体
            WechatPayQueryResult response = null;
            try {
                response = HkrXmlUtil.readBeanFromXML(responseStr, WechatPayQueryResult.class);
            } catch (Exception e) {
                log.error("解析查询结果错误", e);
                throw new HkrBusinessException();
            }

            /**
             * 微信返回状态说明，注意，订单不存在时不会返回如下信息。
             * SUCCESS—支付成功
             * REFUND—转入退款
             * NOTPAY—未支付
             * CLOSED—已关闭
             * REVOKED—已撤销（刷卡支付）
             * USERPAYING--用户支付中
             * PAYERROR--支付失败(其他原因，如银行返回失败)
             */
            PaymentQueryBO resultPaymentQueryBO;
            if (WeChatConstant.WECHAT_SUCCESS.equals(response.getReturnCode())
                    && WeChatConstant.WECHAT_SUCCESS.equals(response.getResultCode())) {
                if (WeChatConstant.WECHAT_SUCCESS.equals(response.getTradeState())) {
                    resultPaymentQueryBO = PaymentQueryBO.getSuccessInstance(response.getOutTradeNo(), response.getTransactionId());
                    resultPaymentQueryBO.setBuyerId(response.getOpenid());
                    // 微信返回单位是分，转换成元
                    resultPaymentQueryBO.setTradeTotalFee(new BigDecimal(response.getTotalFee()).divide(new BigDecimal(NOMBER_100)).toString());
                    resultPaymentQueryBO.setPaymentSource(paymentSource.getCode());
                } else if (WeChatConstant.WECHAT_REFUND.equals(response.getTradeState())
                        || WeChatConstant.WECHAT_CLOSED.equals(response.getTradeState())
                        || WeChatConstant.WECHAT_REVOKED.equals(response.getTradeState())
                        || WeChatConstant.WECHAT_PAYERROR.equals(response.getTradeState())) {
                    resultPaymentQueryBO = PaymentQueryBO.getFailInstance(response.getOutTradeNo(), response.getTransactionId());
                } else {
                    resultPaymentQueryBO = PaymentQueryBO.getPayingInstance(response.getOutTradeNo(), response.getTransactionId());
                }
            } else if (WeChatConstant.WECHAT_SUCCESS.equals(response.getReturnCode())
                    && WeChatConstant.WECHAT_FAIL.equals(response.getResultCode())) {
                if (WeChatConstant.WECHAT_ORDERNOTEXIST.equals(response.getErrCode())) {
                    // 不存在该订单时当失败处理。
                    resultPaymentQueryBO = PaymentQueryBO.getFailInstance(response.getOutTradeNo(), response.getTransactionId());
                } else {
                    log.error("查询支付结果错误");
                    throw new HkrBusinessException();
                }
            } else {
                log.error("查询支付结果错误");
                throw new HkrBusinessException();
            }

            final String resultStr = responseStr;
            //记录结果日志
            ThreadPoolTool.execute(() -> {
                PaymentResultLogDO paymentResultLogDO = PaymentResultLogDO.buildFromPaymentQueryBO(resultPaymentQueryBO);
                paymentResultLogDO.setLogType(PaymentResultLogTypeEnum.QUERY.getCode());
                paymentResultLogDO.setResult(resultStr);
                paymentResultLogRepository.create(paymentResultLogDO);
            });

            return resultPaymentQueryBO;

        } catch (IOException e) {
            log.error("查询支付结果错误");
            throw new HkrBusinessException();
        }
    }

    /**
     * 关闭微信订单
     *
     * @param paymentSource
     * @param outTradeNo
     * @return
     */
    public boolean weChatCloseOrder(PaymentSourceEnum paymentSource, String outTradeNo) {
        try {
            String responseStr = null;
            AppInfo appInfo = null;
            /**
             * 构造请求参数，构造函数中带有签名
             */
            appInfo = wechatAppInfo;
            WechatCloseOrderParameter wechatCloseOrderParameter = new WechatCloseOrderParameter(appInfo, outTradeNo);
            String xmlParam = HkrXmlUtil.toXml(wechatCloseOrderParameter, WechatCloseOrderParameter.class);
            responseStr = WechatHttpClientUtil.wechatPost(WechatConfigure.CLOSEORDER_API, xmlParam);

            try {
                if (!Signature.checkIsSignValidFromResponseString(responseStr, appInfo.getKey())) {
                    return false;
                }
            } catch (Exception e) {
                log.error("验签过程发生异常", e);
                return false;
            }

            // xml 转换成实体
            WechatCloseOrderResult response = HkrXmlUtil.readBeanFromXML(responseStr, WechatCloseOrderResult.class);

            // 正常关闭时返回true
            if (WeChatConstant.WECHAT_SUCCESS.equals(response.getReturnCode()) && WeChatConstant.WECHAT_SUCCESS.equals(response.getResultCode())) {
                return true;
            }
            // 如果订单已经关闭，保持接口幂等性，返回true
            if (WeChatConstant.WECHAT_SUCCESS.equals(response.getReturnCode()) && WeChatConstant.WECHAT_ORDERCLOSED.equals(response.getErrCode())) {
                return true;
            }
            // 其它情况返回失败
            return false;

        } catch (IOException e) {
            log.error("关闭微信订单时发生异常", e);
            return false;
        }
    }

    /**
     * @param paymentSource
     * @param outTradeNo
     * @param outRefundNo
     * @param totalFee  BigDecimal  单位是元
     * @param refundFee  BigDecimal  单位是元
     * @param refund_desc
     * @return true/false 出现超时
     */
    public InnerResultBO weChatRefund(PaymentSourceEnum paymentSource, String outTradeNo, String tradeNo, String outRefundNo, BigDecimal totalFee, BigDecimal refundFee, String refund_desc){
        try {
            String responseStr = null;
            AppInfo appInfo = null;
            int totalFeeFen = totalFee.multiply(new BigDecimal(NOMBER_100)).intValue();
            int refundFeeFen = refundFee.multiply(new BigDecimal(NOMBER_100)).intValue();
            /**
             * 构造请求参数，构造函数中带有签名
             */
            appInfo = wechatAppInfo;
            WechatRefundParameter wechatRefundParameter = new WechatRefundParameter(appInfo, outTradeNo, tradeNo, outRefundNo, totalFeeFen, refundFeeFen, refund_desc);
            String xmlParam = HkrXmlUtil.toXml(wechatRefundParameter, WechatRefundParameter.class);
            responseStr = WechatHttpClientUtil.wechatPost(WechatConfigure.REFUND_API, xmlParam);

            try {
                if (!Signature.checkIsSignValidFromResponseString(responseStr, appInfo.getKey())) {
                    log.error("微信退款验证签名错误");
                    return InnerResultBO.getFailInstants();
                }
            } catch (Exception e) {
                log.error("微信退款验签过程发生异常", e);
                return InnerResultBO.getFailInstants();
            }

            final String resultStr = responseStr;
            //记录结果日志
            ThreadPoolTool.execute(() -> {
                PaymentResultLogDO paymentResultLogDO = PaymentResultLogDO.buildFromBase(outTradeNo, outRefundNo, paymentSource.getCode());
                paymentResultLogDO.setLogType(PaymentResultLogTypeEnum.REFUND.getCode());
                paymentResultLogDO.setResult(resultStr);
                paymentResultLogRepository.create(paymentResultLogDO);
            });

            // xml 转换成实体
            WechatRefundResult response = HkrXmlUtil.readBeanFromXML(responseStr, WechatRefundResult.class);

            // 正常关闭时返回true
            if (WeChatConstant.WECHAT_SUCCESS.equals(response.getReturn_code())
                    && WeChatConstant.WECHAT_SUCCESS.equals(response.getResult_code())) {
                return InnerResultBO.getSuccessInstants();
            }
            // 其它情况返回失败
            return InnerResultBO.getFailInstants();
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            log.error("微信退款时发生超时", e);
            return InnerResultBO.getTimeOutInstants();
        } catch (Exception e){
            log.error("微信退款时发生异常", e);
            return InnerResultBO.getFailInstants();
        }
    }

    public boolean weChatCheck(Map<String, Object> reqMap) {
        return Signature.checkIsSignValidFromResponseString(reqMap, wechatAppInfo.getKey());
    }
}
