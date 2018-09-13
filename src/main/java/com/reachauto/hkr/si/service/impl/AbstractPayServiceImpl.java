package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.utils.ApplicationContextTool;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.cache.PaymentIdGenerator;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.dto.PayParaDTO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.pojo.vo.PayVO;
import com.reachauto.hkr.si.service.PayQueryService;
import com.reachauto.hkr.si.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2018/1/16.
 */
public abstract class AbstractPayServiceImpl implements PayService {

    @Autowired
    protected PaymentDetailRepository paymentDetailRepository;

    @Autowired
    protected PaymentIdGenerator paymentIdGenerator;

    @Value("${env.tag}")
    private String envTag;

    @Override
    public PayVO pay(PayParaDTO payParaDTO) {
        List<PaymentDetailDO> paymentDetailDOList = paymentDetailRepository.findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        PaymentDetailDO paymentDetailDO = confirmHistoryTrade(paymentDetailDOList, payParaDTO);
        return pay(paymentDetailDO);
    }

    protected abstract PayVO pay(PaymentDetailDO paymentDetailDO);

    private PaymentDetailDO confirmHistoryTrade(List<PaymentDetailDO> paymentDetailDOList, PayParaDTO payParaDTO) {
        if (ObjectUtils.isEmpty(paymentDetailDOList)) {
            return createPaymentDetail(payParaDTO);
        }
        PaymentDetailDO paymentDetailDOTemp = null;
        //存在支付流水记录
        for (PaymentDetailDO paymentDetailDO : paymentDetailDOList) {
            tradeSuccess(paymentDetailDO);
            tradeSettlement(paymentDetailDO);
            PaymentDetailDO paymentDetailDOTempOne = tradeProcessing(paymentDetailDO, payParaDTO);
            if (paymentDetailDOTemp == null) {
                paymentDetailDOTemp = paymentDetailDOTempOne;
            }
        }
        if (paymentDetailDOTemp == null) {
            //创建新的流水支付
            return createPaymentDetail(payParaDTO);
        }
        return paymentDetailDOTemp;
    }

    protected void tradeSuccess(PaymentDetailDO paymentDetailDO) {
        if (paymentDetailDO.isSuccess() || paymentDetailDO.isRefunding() || paymentDetailDO.isRefunded()) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_FINISHED, "order.finished");
        }
    }

    protected void tradeSettlement(PaymentDetailDO paymentDetailDO) {
        if (paymentDetailDO.isSettlement()) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_SETTLEMENT, "order.settlement");
        }
    }

    private PaymentDetailDO tradeProcessing(PaymentDetailDO paymentDetailDO, PayParaDTO payParaDTO) {
        if (paymentDetailDO.isProcessing()) {
            if (Objects.equals(paymentDetailDO.getPaymentSource(), payParaDTO.getPaymentSource())) {
                //支付方式相同
                if (payParaDTO.getPrice().compareTo(paymentDetailDO.getTradeTotalFee()) == 0) {
                    //金额相同 还用以前的流水
                    return paymentDetailDO;
                } else {
                    //金额不同 创建新流水
                    return null;
                }
            } else {
                //不是同一种支付方式 查询
                PayQueryService payQueryService = getPayQueryService(paymentDetailDO.getPaymentSource());
                if (payQueryService == null) {
                    return null;
                }
                PaymentDetailDO paymentDetailDOResult = payQueryService.orderQueryFull(paymentDetailDO);
                //已经成功，终止
                tradeSuccess(paymentDetailDOResult);
                //否则创建新流水
            }
        }
        return null;
    }

    private PayQueryService getPayQueryService(Integer paymentSource) {
        PaymentSourceEnum paymentSourceEnum = PaymentSourceEnum.getType(paymentSource);
        if (paymentSourceEnum == null || paymentSourceEnum.getQueryClazz() == null) {
            return null;
        }
        return (PayQueryService) ApplicationContextTool.get(paymentSourceEnum.getQueryClazz());

    }

    protected PaymentDetailDO createPaymentDetail(PayParaDTO payParaDTO) {
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setOrderId(payParaDTO.getOrderId());
        paymentDetailDO.setUserId(payParaDTO.getUserId());
        paymentDetailDO.setTradeTotalFee(payParaDTO.getPrice());
        paymentDetailDO.setOrderType(payParaDTO.getOrderType());
        paymentDetailDO.setTradeCTime(new Date());
        paymentDetailDO.setPaymentSource(payParaDTO.getPaymentSource());
        paymentDetailDO.setPaymentType(payParaDTO.getPaymentType());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        paymentDetailDO.setPaymentId(paymentIdGenerator.getAPaymentId());
        paymentDetailDO.setBizCallbackUrl(payParaDTO.getCallbackUrl());
        paymentDetailDO.setFinishBizCallback(payParaDTO.getFinishBizCallback());
        paymentDetailDO.setOpenId(payParaDTO.getOpenId());
        paymentDetailDO.setEnvTag(envTag);
        paymentDetailRepository.create(paymentDetailDO);
        return paymentDetailDO;
    }

}
