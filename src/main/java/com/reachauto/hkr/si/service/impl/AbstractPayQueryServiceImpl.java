package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.BizCallbackService;
import com.reachauto.hkr.si.service.PayQueryService;
import com.reachauto.hkr.si.utils.ThreadPoolTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Administrator on 2018/1/16.
 */
public abstract class AbstractPayQueryServiceImpl implements PayQueryService {

    @Autowired
    protected PaymentDetailRepository paymentDetailRepository;

    @Autowired
    protected BizCallbackService bizCallbackService;

    @Autowired
    protected BalanceManager balanceManager;

    @Override
    public PaymentDetailDO orderQuerySimple(String paymentId){
        return paymentDetailRepository.findById(Long.parseLong(paymentId));
    }

    protected void updatePaymentDetailDOByQueryResult(PaymentDetailDO paymentDetailDO, PaymentQueryBO paymentQueryBO){

        /**
         * 如果paymentQueryBO不为空，并且处于支付成功/支付失败状态，更新本地数据库，回调业务系统
         */
        if(Objects.nonNull(paymentQueryBO)) {
            if(paymentQueryBO.isSuccess()){
                paymentDetailDO.setBuyerId(paymentQueryBO.getBuyerId());
                paymentDetailDO.setTradeTotalFee(new BigDecimal(paymentQueryBO.getTradeTotalFee()));
                paymentDetailDO.setTradeNo(paymentQueryBO.getTradeNo());
                // 如果是充值订单，处理余额
                InnerResultBO innerResultBO = execBalance(paymentDetailDO);
                if(innerResultBO.isSuccessed()){
                    paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
                }else{
                    // 超时或者失败都保持不变
                    return;
                }
            }else if(paymentQueryBO.isFail()){
                paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
            }else {
                return ;
            }
            if(paymentDetailRepository.finishTrade(paymentDetailDO) > 0){
                // 更新成功后异步发起回调
                ThreadPoolTool.execute(() -> bizCallbackService.bizCallBack(paymentDetailDO));
            }
        }
    }

    /**
     * 如果是充值，处理余额，注意：第三方成功时才调用此方法
     * @param paymentDetailDO
     * @return
     */
    private InnerResultBO execBalance(PaymentDetailDO paymentDetailDO) {
        // 不是充值直接返回成功
        if (!PaymentTypeEnum.RECHARGE.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
            return InnerResultBO.getSuccessInstants();
        }

        // 如果是充值，加余额处理
        InnerResultBO balanceResultBO = balanceManager.modifyBalance(String.valueOf(paymentDetailDO.getPaymentId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                paymentDetailDO.getTradeTotalFee(), paymentDetailDO.getRemarks());
        return balanceResultBO;
    }
}
