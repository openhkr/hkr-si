package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.WeChatManager;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by Administrator on 2018/1/16.
 */
@Service
public class WeChatPayQueryServiceImpl extends AbstractPayQueryServiceImpl {

    @Autowired
    private WeChatManager weChatManager;

    @Override
    public PaymentDetailDO orderQueryFull(String paymentId) {
        // 微信公众号根据paymentId查询支付信息
        PaymentDetailDO paymentDetailDO = this.orderQuerySimple(paymentId);
        return orderQueryFull(paymentDetailDO);
    }

    @Override
    public PaymentDetailDO orderQueryFull(PaymentDetailDO paymentDetailDO) {
        // 如果是结束状态，直接返回
        if (Objects.isNull(paymentDetailDO) || paymentDetailDO.hasFinishPayed()) {
            return paymentDetailDO;
        }

        PaymentQueryBO paymentQueryBO = weChatManager.weChatOrderQuery(PaymentSourceEnum.WECHAT, paymentDetailDO.getPaymentId(), "");

        /**
         * 更新支付状态并发起回调
         */
        updatePaymentDetailDOByQueryResult(paymentDetailDO, paymentQueryBO);
        return paymentDetailDO;
    }
}
