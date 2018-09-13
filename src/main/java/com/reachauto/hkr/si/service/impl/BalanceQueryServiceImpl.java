package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.constant.BalanceRecordStatusConstants;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-22 11:41
 */
@Slf4j
@Service
public class BalanceQueryServiceImpl extends AbstractPayQueryServiceImpl {


    @Override
    public PaymentDetailDO orderQueryFull(String paymentId) {
        PaymentDetailDO paymentDetailDO = this.orderQuerySimple(paymentId);
        return orderQueryFull(paymentDetailDO);
    }

    @Override
    public PaymentDetailDO orderQueryFull(PaymentDetailDO paymentDetailDO) {
        // 如果是结束状态，直接返回
        if (Objects.isNull(paymentDetailDO) || paymentDetailDO.hasFinishPayed()) {
            return paymentDetailDO;
        }

        // 查询支付结果
        Integer balanceStatus = balanceManager.recordStatusQuery(String.valueOf(paymentDetailDO.getPaymentId()));
        log.info("查询余额支付{}结果：{}", paymentDetailDO.getId(), String.valueOf(balanceStatus));

        if(balanceStatus.intValue() == BalanceRecordStatusConstants.BALANCE_RECORD_STATUS_SUCCESS){
            // 结果成功的情况下回滚该记录
            InnerResultBO innerResultBO = balanceManager.rollbackModifyRecord(String.valueOf(paymentDetailDO.getPaymentId()));
            if(innerResultBO.isSuccessed()){
                paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
                paymentDetailRepository.finishTrade(paymentDetailDO);
            }
        }else{
            // 没有记录说明未扣款
            paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
            paymentDetailRepository.finishTrade(paymentDetailDO);
        }

        return paymentDetailDO;
    }
}
