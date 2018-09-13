package com.reachauto.hkr.si.entity;

import com.reachauto.hkr.common.entity.Entity;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangshuo
 */
@Data
public class RefundRecordDO extends Entity {

    private Long paymentId;

    private String orderId;

    private Integer orderType;

    private String tradeNo;

    private Integer tradeStatus;

    private String buyerId;

    private BigDecimal amount;

    public RefundRecordDO(){}

    public RefundRecordDO(RefundDTO refundDTO){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        setPaymentId(Long.parseLong(paymentDetailDO.getPaymentId()));
        setOrderId(paymentDetailDO.getOrderId());
        setOrderType(paymentDetailDO.getOrderType());
        setTradeNo(paymentDetailDO.getTradeNo());
        setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        setAmount(refundDTO.getAmount());
        setRemarks(refundDTO.getDesc());
    }
}