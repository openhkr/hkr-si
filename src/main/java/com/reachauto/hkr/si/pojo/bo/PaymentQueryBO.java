package com.reachauto.hkr.si.pojo.bo;

import lombok.Data;

import java.util.Objects;

/**
 * Created by Administrator on 2018/1/15.
 */
@Data
public class PaymentQueryBO {

    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAIL = 2;
    private static final int STATUS_PAYING = 4;

    private Integer tradeStatus;
    private String outTradeNo;
    private String tradeNo;
    private String buyerId;
    private String tradeTotalFee;
    private Integer paymentSource;

    public static PaymentQueryBO getFailInstance(String outTradeNo, String tradeNo) {
        PaymentQueryBO paymentQueryBO = getInstance(outTradeNo, tradeNo);
        paymentQueryBO.setTradeStatus(STATUS_FAIL);
        return paymentQueryBO;
    }

    public static PaymentQueryBO getSuccessInstance(String outTradeNo, String tradeNo) {
        PaymentQueryBO paymentQueryBO = getInstance(outTradeNo, tradeNo);
        paymentQueryBO.setTradeStatus(STATUS_SUCCESS);
        return paymentQueryBO;
    }

    public static PaymentQueryBO getPayingInstance(String outTradeNo, String tradeNo) {
        PaymentQueryBO paymentQueryBO = getInstance(outTradeNo, tradeNo);
        paymentQueryBO.setTradeStatus(STATUS_PAYING);
        return paymentQueryBO;
    }

    private static PaymentQueryBO getInstance(String outTradeNo, String tradeNo) {
        PaymentQueryBO paymentQueryBO = new PaymentQueryBO();
        paymentQueryBO.setOutTradeNo(outTradeNo);
        paymentQueryBO.setTradeNo(tradeNo);
        return paymentQueryBO;
    }

    public boolean isSuccess() {
        return Objects.equals(STATUS_SUCCESS, this.getTradeStatus());
    }

    public boolean isFail() {
        return Objects.equals(STATUS_FAIL, this.getTradeStatus());
    }

    public boolean isPaying() {
        return Objects.equals(STATUS_PAYING, this.getTradeStatus());
    }
}
