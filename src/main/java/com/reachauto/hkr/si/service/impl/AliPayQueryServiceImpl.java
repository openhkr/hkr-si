package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.AliPayManager;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by Administrator on 2018/1/16.
 */
@Service
public class AliPayQueryServiceImpl extends AbstractPayQueryServiceImpl {

    @Autowired
    private AliPayManager aliPayManager;

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

        // 查询支付宝
        PaymentQueryBO paymentQueryBO = aliPayManager.query(paymentDetailDO.getPaymentId(), "");

        /**
         * 更新支付状态并发起回调
         */
        updatePaymentDetailDOByQueryResult(paymentDetailDO, paymentQueryBO);
        return paymentDetailDO;
    }
}
