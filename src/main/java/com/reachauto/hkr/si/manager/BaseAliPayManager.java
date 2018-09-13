package com.reachauto.hkr.si.manager;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.config.NotifyUrlProperties;
import com.reachauto.hkr.si.constant.AliPayConstants;
import com.reachauto.hkr.si.entity.PaymentResultLogDO;
import com.reachauto.hkr.si.persistence.PaymentResultLogRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import com.reachauto.hkr.si.pojo.enu.PaymentResultLogTypeEnum;
import com.reachauto.hkr.si.utils.ThreadPoolTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Administrator on 2018/1/15.
 */
@Slf4j
@Component
@EnableConfigurationProperties(NotifyUrlProperties.class)
public abstract class BaseAliPayManager {

    protected NotifyUrlProperties notifyUrl;

    protected PaymentResultLogRepository paymentResultLogRepository;

    public abstract AlipayClient getAlipayClient();

    public abstract Integer getPaymentSource();

    public abstract boolean check(Map<String, String> params);

    public String pay(String subject, String body, String price, String outTradeNo, String evnFlag) {
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

        try {
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            //商户网站唯一订单号
            model.setOutTradeNo(outTradeNo);
            //商品名称
            model.setSubject(subject);
            //商品详情
            model.setBody(body);
            //商品金额
            model.setTotalAmount(price);

            model.setTimeoutExpress(AliPayConstants.TIME_OUT_EXPRESS);
            model.setProductCode(AliPayConstants.PRODUCT_CODE);
            model.setGoodsType(AliPayConstants.GOOD_TYPE);

            request.setBizModel(model);
            //回调地址
            request.setNotifyUrl(notifyUrl.getAlipay().concat("/").concat(evnFlag == null ? "" : evnFlag));
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = getAlipayClient().sdkExecute(request);
            log.debug("支付宝加密结果：" + response.getBody());
            return response.getBody();
        } catch (AlipayApiException e) {
            log.error("支付参数加密失败.", e);
            return null;
        }
    }

    /**
     * @param outTradeNo 商户订单号
     * @param tradeNo    交易流水号
     *                   订单支付时传入的商户订单号,和支付宝交易号不能同时为空。trade_no,out_trade_no如果同时存在优先取trade_no
     */
    public PaymentQueryBO query(String outTradeNo, String tradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //设置业务参数
        request.setBizContent(
                "{\"out_trade_no\":\"".concat(outTradeNo == null ? "" : outTradeNo).concat("\",")
                        .concat("\"trade_no\":\"").concat(tradeNo == null ? "" : tradeNo).concat("\"}")
        );

        AlipayTradeQueryResponse response;
        try {
            response = getAlipayClient().execute(request);
        } catch (AlipayApiException e) {
            log.error("查询支付结果错误, {}", e);
            throw new HkrBusinessException();
        }

        if (response == null) {
            throw new HkrBusinessException();
        }
        PaymentQueryBO paymentQueryBO;
        if (AliPayConstants.ALI_BIZ_ERR_CODE.equals(response.getCode())) {
            if (AliPayConstants.ALI_BIZ_ERR_SUB_CODE_TRADE_NOT_EXIST.equals(response.getSubCode())) {
                paymentQueryBO = PaymentQueryBO.getPayingInstance(outTradeNo, tradeNo);
            } else {
                throw new HkrBusinessException();
            }
        } else if (AliPayConstants.ALI_SUCCESS_CODE.equals(response.getCode())) {
            if (AliPayConstants.WAIT_BUYER_PAY.equals(response.getTradeStatus())) {
                paymentQueryBO = PaymentQueryBO.getPayingInstance(response.getOutTradeNo(), response.getTradeNo());
            } else if (AliPayConstants.TRADE_CLOSED.equals(response.getTradeStatus())) {
                paymentQueryBO = PaymentQueryBO.getFailInstance(response.getOutTradeNo(), response.getTradeNo());
            } else {
                paymentQueryBO = PaymentQueryBO.getSuccessInstance(response.getOutTradeNo(), response.getTradeNo());
            }
        } else {
            throw new HkrBusinessException();
        }


        paymentQueryBO.setBuyerId(response.getBuyerUserId());
        paymentQueryBO.setTradeTotalFee(response.getTotalAmount());
        paymentQueryBO.setPaymentSource(getPaymentSource());

        //记录结果日志
        ThreadPoolTool.execute(() -> {
            PaymentResultLogDO paymentResultLogDO = PaymentResultLogDO.buildFromPaymentQueryBO(paymentQueryBO);
            paymentResultLogDO.setLogType(PaymentResultLogTypeEnum.QUERY.getCode());
            paymentResultLogDO.setResult(response.getBody());
            paymentResultLogRepository.create(paymentResultLogDO);
        });

        return paymentQueryBO;
    }

    /**
     * @param outTradeNo 商户订单号
     * @param tradeNo    交易流水号
     *                   订单支付时传入的商户订单号,和支付宝交易号不能同时为空。trade_no,out_trade_no如果同时存在优先取trade_no
     */
    public boolean close(String outTradeNo, String tradeNo) {
        log.info("关闭交易：outTradeNo:{}, tradeNo:{}", outTradeNo, tradeNo);
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent("{"
                .concat("\"trade_no\":\"").concat(tradeNo == null ? "" : outTradeNo).concat("\",")
                .concat("\"out_trade_no\":\"").concat(outTradeNo == null ? "" : outTradeNo).concat("\",")
                .concat("\"operator_id\":\"").concat("\"  }"));
        AlipayTradeCloseResponse response;
        try {
            response = getAlipayClient().execute(request);
        } catch (AlipayApiException e) {
            log.error("关系交易失败, {}", e);
            return false;
        }
        if (response == null || !AliPayConstants.ALI_SUCCESS_CODE.equals(response.getCode()) || !response.isSuccess()) {
            log.error("关系交易失败, {}", response == null ? "" : response.getBody());
            return false;
        }

        //todo 判断失败原因

        return true;
    }

    /**
     * @param outTradeNo  商户订单号
     * @param tradeNo     交易流水号
     *                    订单支付时传入的商户订单号,和支付宝交易号不能同时为空。trade_no,out_trade_no如果同时存在优先取trade_no
     * @param outRefundNo 商户退款编号
     * @param refundFee   退款金额
     * @param description 退款原因
     * @return
     */
    public InnerResultBO refund(String outTradeNo, String tradeNo, String outRefundNo, String refundFee, String description) {
        log.info("申请退款：outTradeNo:{}, tradeNo:{}, outRefundNo:{}, refundFee:{}, description:{}", outTradeNo, tradeNo, outRefundNo, refundFee, description);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent("{"
                .concat("\"out_trade_no\":\"").concat(outTradeNo == null ? "" : outTradeNo).concat("\",")
                .concat("\"trade_no\":\"").concat(tradeNo == null ? "" : tradeNo).concat("\",")
                .concat("\"refund_amount\":").concat(refundFee).concat(",")
                .concat("\"refund_reason\":\"").concat(description == null ? "" : description).concat("\",")
                .concat("\"out_request_no\":\"").concat(outRefundNo).concat("\"")
                .concat("  }"));
        AlipayTradeRefundResponse response;
        try {
            response = getAlipayClient().execute(request);
        } catch (AlipayApiException e) {
            log.error("关系交易失败, {}", e);
            return InnerResultBO.getTimeOutInstants();
        }

        if (response == null) {
            return InnerResultBO.getFailInstants();
        }
        if (!AliPayConstants.ALI_SUCCESS_CODE.equals(response.getCode())) {
            return InnerResultBO.getFailInstants();
        }
        //记录结果日志
        ThreadPoolTool.execute(() -> {
            PaymentResultLogDO paymentResultLogDO = new PaymentResultLogDO();
            paymentResultLogDO.setTradeNo(tradeNo);
            paymentResultLogDO.setOutTradeNo(outTradeNo);
            paymentResultLogDO.setPaymentSource(getPaymentSource());
            paymentResultLogDO.setLogType(PaymentResultLogTypeEnum.REFUND.getCode());
            paymentResultLogDO.setResult(response.getBody());
            paymentResultLogRepository.create(paymentResultLogDO);
        });
        return InnerResultBO.getSuccessInstants();
    }
}
