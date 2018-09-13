package com.reachauto.hkr.si.entity;

import com.reachauto.hkr.common.entity.Entity;
import com.reachauto.hkr.si.pojo.bo.CallbackBO;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Administrator on 2018/1/15.
 */
@Data
@ToString(callSuper = true)
public class PaymentResultLogDO extends Entity {
    private String outTradeNo;
    private String tradeNo;
    private Integer paymentSource;
    private Integer logType;
    private String result;

    public static PaymentResultLogDO buildFromPaymentQueryBO(PaymentQueryBO paymentQueryBO) {
        if (paymentQueryBO == null) {
            return null;
        }
        PaymentResultLogDO paymentResultLogDO= new PaymentResultLogDO();
        paymentResultLogDO.setOutTradeNo(paymentQueryBO.getOutTradeNo());
        paymentResultLogDO.setTradeNo(paymentQueryBO.getTradeNo());
        paymentResultLogDO.setPaymentSource(paymentQueryBO.getPaymentSource());
        return paymentResultLogDO;
    }

    public static PaymentResultLogDO buildFromAliPayCallbackBO(CallbackBO callbackBO) {
        if (callbackBO == null) {
            return null;
        }
        PaymentResultLogDO paymentResultLogDO= new PaymentResultLogDO();
        paymentResultLogDO.setOutTradeNo(callbackBO.getOutTradeNo());
        paymentResultLogDO.setTradeNo(callbackBO.getTradeNo());
        paymentResultLogDO.setPaymentSource(callbackBO.getPaymentSource());
        return paymentResultLogDO;
    }

    /**
     * 根据基本信息创建
     * @param outTradeNo
     * @param tradeNo
     * @param paymentSource
     * @return
     */
    public static PaymentResultLogDO buildFromBase(String outTradeNo, String tradeNo, Integer paymentSource) {
        PaymentResultLogDO paymentResultLogDO= new PaymentResultLogDO();
        paymentResultLogDO.setOutTradeNo(outTradeNo);
        paymentResultLogDO.setTradeNo(tradeNo);
        paymentResultLogDO.setPaymentSource(paymentSource);
        return paymentResultLogDO;
    }
}
