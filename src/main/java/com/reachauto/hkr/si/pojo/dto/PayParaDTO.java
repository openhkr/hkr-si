package com.reachauto.hkr.si.pojo.dto;

import com.reachauto.hkr.si.pojo.enu.FinishBizCallbackEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.parameter.PaymentBalanceParameter;
import com.reachauto.hkr.si.pojo.parameter.PaymentRechargeParameter;
import com.reachauto.hkr.si.pojo.parameter.PaymentThirdParameter;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/1/16.
 */
@Data
public class PayParaDTO {

    private String orderId;
    private Integer orderType;
    private Integer paymentSource;
    private Integer paymentType;
    private BigDecimal price;
    private Integer finishBizCallback;
    private String callbackUrl;
    private String userId;
    private String openId;

    public static PayParaDTO buildFromPayParameter(PaymentThirdParameter paymentThirdParameter) {
        if (paymentThirdParameter == null) {
            return null;
        }
        PayParaDTO payParaDTO= new PayParaDTO();
        payParaDTO.setOrderId(paymentThirdParameter.getOrderId());
        payParaDTO.setOrderType(Integer.valueOf(paymentThirdParameter.getOrderType()));
        payParaDTO.setPaymentSource(Integer.valueOf(paymentThirdParameter.getPaymentSource()));
        payParaDTO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        payParaDTO.setPrice(paymentThirdParameter.getPrice());
        payParaDTO.setCallbackUrl(paymentThirdParameter.getCallbackUrl());
        payParaDTO.setFinishBizCallback(FinishBizCallbackEnum.UN_CALL_BACK_.getCode());
        return payParaDTO;
    }

    public static PayParaDTO buildFromBalancePayParameter(PaymentBalanceParameter paymentBalanceParameter) {
        if (paymentBalanceParameter == null) {
            return null;
        }
        PayParaDTO payParaDTO= new PayParaDTO();
        payParaDTO.setOrderId(paymentBalanceParameter.getOrderId());
        payParaDTO.setOrderType(Integer.valueOf(paymentBalanceParameter.getOrderType()));
        payParaDTO.setPaymentSource(Integer.valueOf(paymentBalanceParameter.getPaymentSource()));
        payParaDTO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        payParaDTO.setPrice(paymentBalanceParameter.getPrice());
        payParaDTO.setCallbackUrl("-");
        payParaDTO.setFinishBizCallback(FinishBizCallbackEnum.HAS_CALL_BACK.getCode());
        return payParaDTO;
    }

    public static PayParaDTO buildFromRechargePayParameter(PaymentRechargeParameter paymentRechargeParameter) {
        if (paymentRechargeParameter == null) {
            return null;
        }
        PayParaDTO payParaDTO= new PayParaDTO();
        payParaDTO.setOrderId(paymentRechargeParameter.getOrderId());
        payParaDTO.setOrderType(Integer.valueOf(paymentRechargeParameter.getOrderType()));
        payParaDTO.setPaymentSource(Integer.valueOf(paymentRechargeParameter.getPaymentSource()));
        payParaDTO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        payParaDTO.setPrice(paymentRechargeParameter.getPrice());
        payParaDTO.setCallbackUrl(paymentRechargeParameter.getCallbackUrl());
        payParaDTO.setFinishBizCallback(FinishBizCallbackEnum.UN_CALL_BACK_.getCode());
        return payParaDTO;
    }
}
