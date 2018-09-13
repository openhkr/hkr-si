package com.reachauto.hkr.si.entity;

import com.reachauto.hkr.common.entity.Entity;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Administrator on 2016/5/11.
 */
@Data
public class PaymentDetailDO extends Entity {

    /**
     * 主键
     */
    private String paymentId;

    /**
     * 订单ID
     */
    private String orderId;

    private String userId;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 交易流水号
     */
    private String tradeNo;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 支付账号
     */
    private String buyerId;

    /**
     * 交易金额
     */
    private BigDecimal tradeTotalFee;

    /**
     * 交易创建时间
     */
    private Date tradeCTime;

    private Integer paymentSource;

    /**
     * 订单类型，1：支付订单，2：充值订单
     */
    private Integer paymentType;

    /**
     * 是否完成业务回调,1.未完成,2.已完成
     */
    private Integer finishBizCallback;

    /**
     * 业务回调地址
     */
    private String bizCallbackUrl;

    private String openId;

    private String envTag;

    public boolean isSettlement() {
        return Objects.equals(TradeStatusEnum.TRADE_SETTLEMENT.getCode(), this.tradeStatus);
    }

    public boolean isSuccess() {
        return Objects.equals(TradeStatusEnum.TRADE_SUCCESS.getCode(), this.tradeStatus);
    }

    public boolean isProcessing() {
        return Objects.equals(TradeStatusEnum.TRADE_PROCESSING.getCode(), this.tradeStatus);
    }

    public boolean isRefunding() {
        return Objects.equals(TradeStatusEnum.TRADE_REFUNDING.getCode(), this.tradeStatus)
                || Objects.equals(TradeStatusEnum.TRADE_MANUAL_REFUNDING.getCode(), this.tradeStatus);
    }

    public boolean isRefunded() {
        return Objects.equals(TradeStatusEnum.TRADE_REFUNDED.getCode(), this.tradeStatus)
                || Objects.equals(TradeStatusEnum.TRADE_MANUAL_REFUNDED.getCode(), this.tradeStatus);
    }

    public boolean hasFinishPayed() {
        return (tradeStatus.intValue() == TradeStatusEnum.TRADE_SUCCESS.getCode()
                || tradeStatus.intValue() == TradeStatusEnum.TRADE_FAIL.getCode()
                || tradeStatus.intValue() == TradeStatusEnum.TRADE_REFUNDING.getCode()
                || tradeStatus.intValue() == TradeStatusEnum.TRADE_REFUNDED.getCode()
                || tradeStatus.intValue() == TradeStatusEnum.TRADE_MANUAL_REFUNDING.getCode()
                || tradeStatus.intValue() == TradeStatusEnum.TRADE_MANUAL_REFUNDED.getCode());
    }
}
