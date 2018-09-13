package com.reachauto.hkr.si.pojo.bo;

import lombok.Data;

/**
 * @author zhangshuo
 * 回调业务系统BO
 */
@Data
public class BizCallbackBO {

    private String orderId;

    private String userId;

    private String tradeTotalFee;

    private String thirdPayTransactionId;

    /**
     * 1.成功；2.失败
     */
    private Integer tradeStatus;

    private String paymentId;

    private String paymentSource;
}
