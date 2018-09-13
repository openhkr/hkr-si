package com.reachauto.hkr.si.service;

import com.reachauto.hkr.si.entity.PaymentDetailDO;

/**
 * Created by Administrator on 2018/1/16.
 */
public interface PayQueryService {

    /**
     * 订单查询
     * 如果订单处于处理中，结算中状态，会调用第三方确诊最终状态后返回
     * @param paymentId
     * @return
     */
    PaymentDetailDO orderQueryFull(String paymentId);

    /**
     * 订单查询
     * 如果订单处于处理中，结算中状态，会调用第三方确诊最终状态后返回
     * @param paymentDetailDO
     * @return
     */
    PaymentDetailDO orderQueryFull(PaymentDetailDO paymentDetailDO);

    /**
     * 订单查询
     * 仅查询本地表数据并返回
     * @param paymentId
     * @return
     */
    PaymentDetailDO orderQuerySimple(String paymentId);
}
