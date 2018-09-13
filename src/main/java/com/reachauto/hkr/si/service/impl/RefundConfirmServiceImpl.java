package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.entity.RefundRecordDO;
import com.reachauto.hkr.si.manager.*;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.RefundRecordRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.RefundConfirmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by Administrator on 2018/2/2.
 */
@Slf4j
@Service
public class RefundConfirmServiceImpl implements RefundConfirmService {

    @Autowired
    private AliPayManager aliPayManager;

    @Autowired
    private WeChatManager weChatManager;

    @Autowired
    private BalanceManager balanceManager;

    @Autowired
    private RefundRecordRepository refundRecordRepository;

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @Override
    public void confirm(PaymentDetailDO paymentDetailDO) {
        //查退款记录
        RefundRecordDO refundRecordDO = refundRecordRepository.findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));
        if (refundRecordDO == null) {
            afterFail(paymentDetailDO, null);
            return;
        }

        if(Objects.equals(TradeStatusEnum.TRADE_REFUNDING.getCode(), paymentDetailDO.getTradeStatus().intValue())){
            // 普通退款
            // 结束处理标识
            aLiPayRetryRefund(paymentDetailDO, refundRecordDO);
            weChatPayRetryRefund(paymentDetailDO, refundRecordDO);
            balancePayRetryRefund(paymentDetailDO, refundRecordDO);
        }else{
            // 财务退款
            retryManualRefund(paymentDetailDO, refundRecordDO);
        }
    }

    /**
     * 处理支付宝支付/充值退款确认
     *
     * @param paymentDetailDO
     * @param refundRecordDO
     */
    private void aLiPayRetryRefund(PaymentDetailDO paymentDetailDO, RefundRecordDO refundRecordDO) {

        if (!Objects.equals(PaymentSourceEnum.ALI.getCode(), paymentDetailDO.getPaymentSource())) {
            return ;
        }

        // 如果是充值处理余额, 返回是否结束本次处理
        if(rechargeBalanceRefund(paymentDetailDO, refundRecordDO)){
            return ;
        }

        InnerResultBO innerResultBO = aliPayManager.refund(String.valueOf(refundRecordDO.getPaymentId()),
                refundRecordDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                String.valueOf(refundRecordDO.getAmount()),
                refundRecordDO.getRemarks());
        if (innerResultBO.isSuccessed()) {
            afterSuccess(paymentDetailDO);
        }
        return ;
    }

    /**
     * 处理微信支付/充值退款确认
     *
     * @param paymentDetailDO
     * @param refundRecordDO
     */
    private void weChatPayRetryRefund(PaymentDetailDO paymentDetailDO, RefundRecordDO refundRecordDO) {

        if (!Objects.equals(PaymentSourceEnum.WECHAT.getCode(), paymentDetailDO.getPaymentSource())) {
            return ;
        }

        // 如果是充值处理余额, 返回是否结束本次处理
        if(rechargeBalanceRefund(paymentDetailDO, refundRecordDO)){
            return ;
        }

        InnerResultBO innerResultBO = weChatManager.weChatRefund(PaymentSourceEnum.WECHAT,
                String.valueOf(refundRecordDO.getPaymentId()),
                paymentDetailDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getTradeTotalFee(),
                refundRecordDO.getAmount(),
                refundRecordDO.getRemarks());
        if (innerResultBO.isSuccessed()) {
            afterSuccess(paymentDetailDO);
        }
        return ;
    }

    /**
     * 处理余额支付退款确认
     *
     * @param paymentDetailDO
     * @param refundRecordDO
     */
    private void balancePayRetryRefund(PaymentDetailDO paymentDetailDO, RefundRecordDO refundRecordDO) {
        // 不是支付直接返回
        if (!Objects.equals(PaymentTypeEnum.PAYMENT.getCode(), paymentDetailDO.getPaymentType().intValue())) {
            return ;
        }

        if (!Objects.equals(PaymentSourceEnum.COMMON_BALANCE.getCode(), paymentDetailDO.getPaymentSource())
                && !Objects.equals(PaymentSourceEnum.DEPOSIT_BALANCE.getCode(), paymentDetailDO.getPaymentSource())) {
            return ;
        }

        // 调用余额系统加钱
        InnerResultBO innerResultBO = balanceManager.modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundRecordDO.getAmount(), refundRecordDO.getRemarks());

        if (innerResultBO.isSuccessed()) {
            afterSuccess(paymentDetailDO);
        } else if (innerResultBO.isFailed()) {
            afterFail(paymentDetailDO, refundRecordDO);
        }
        return ;
    }

    /**
     * 财务退款确认
     * 仅三方给余额充值财务退款需要定时任务保证
     * @param paymentDetailDO
     * @param refundRecordDO
     * @return
     */
    private void retryManualRefund(PaymentDetailDO paymentDetailDO, RefundRecordDO refundRecordDO){

        if(!Objects.equals(PaymentTypeEnum.RECHARGE.getCode(), paymentDetailDO.getPaymentType().intValue())){
            return ;
        }
        // 如果是充值处理余额, 返回是否结束本次处理
        if(rechargeBalanceRefund(paymentDetailDO, refundRecordDO)){
            return ;
        }
        afterSuccess(paymentDetailDO);
        return ;
    }

    /**
     * 充值退款时调用余额系统减钱的接口，返回是否需要继续处理
     * @param paymentDetailDO
     * @param refundRecordDO
     * 返回是否结束本次处理
     */
    private boolean rechargeBalanceRefund(PaymentDetailDO paymentDetailDO, RefundRecordDO refundRecordDO){
        // 如果是充值处理余额
        if (Objects.equals(PaymentTypeEnum.RECHARGE.getCode(), paymentDetailDO.getPaymentType().intValue())) {
            // 调用余额系统减钱
            InnerResultBO balanceResultBO = balanceManager.modifyBalance(String.valueOf(refundRecordDO.getId()),
                    paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                    refundRecordDO.getAmount().negate(), refundRecordDO.getRemarks());

            if (balanceResultBO.isFailed()) {
                afterFail(paymentDetailDO, refundRecordDO);
                return true;
            } else if (balanceResultBO.isTimeOut()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 退款成功处理
     */
    private void afterSuccess(PaymentDetailDO paymentDetailDO) {
        paymentDetailRepository.refunded(paymentDetailDO);
    }

    /**
     * 退款失败处理
     *
     * @param paymentDetailDO
     * @param refundRecordDO
     */
    private void afterFail(PaymentDetailDO paymentDetailDO, RefundRecordDO refundRecordDO) {
        //支付流水置状态为支付成功
        if (paymentDetailRepository.refundFailed(paymentDetailDO) > 0 && refundRecordDO != null) {
            //删除退款记录
            refundRecordRepository.delete(refundRecordDO.getId());
        }
    }
}
