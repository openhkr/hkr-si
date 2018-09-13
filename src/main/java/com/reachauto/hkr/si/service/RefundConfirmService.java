package com.reachauto.hkr.si.service;

import com.reachauto.hkr.si.entity.PaymentDetailDO;

/**
 * Created by Administrator on 2018/2/2.
 */
public interface RefundConfirmService {

    void confirm(PaymentDetailDO paymentDetailDO);
}
