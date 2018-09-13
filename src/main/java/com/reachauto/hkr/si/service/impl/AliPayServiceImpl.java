package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.AliPayManager;
import com.reachauto.hkr.si.pojo.enu.EnvironmentFlagEnum;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.vo.PayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2018/1/16.
 */
@Service
public class AliPayServiceImpl extends AbstractPayServiceImpl {

    @Autowired
    private AliPayManager aliPayManager;

    @Override
    protected PayVO pay(PaymentDetailDO paymentDetailDO) {
        OrderTypeEnum orderType = OrderTypeEnum.getType(paymentDetailDO.getOrderType());
        String evnFlag = EnvironmentFlagEnum.getType(paymentDetailDO.getEnvTag()).getName();
        String sign = aliPayManager.pay(orderType.getName(), orderType.getName(), paymentDetailDO.getTradeTotalFee().toString(), paymentDetailDO.getPaymentId(), evnFlag);
        if (sign == null) {
            throw new HkrBusinessException(ErrCodeConstant.PAY_ERROR, "pay.error");
        }
        return new PayVO(paymentDetailDO.getPaymentId(), sign);
    }
}
