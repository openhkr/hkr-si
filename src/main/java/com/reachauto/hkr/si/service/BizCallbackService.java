package com.reachauto.hkr.si.service;

import com.reachauto.hkr.si.entity.PaymentDetailDO;

/**
 * Created by Administrator on 2018/1/16.
 */
public interface BizCallbackService {


    /**
     * 回调业务方
     * @param paymentDetailDO
     */
    void bizCallBack(PaymentDetailDO paymentDetailDO);
}
