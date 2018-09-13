package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.entity.RefundRecordDO;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhang.shuo
 */
@Slf4j
@Service
public class BalanceRefundServiceImpl extends AbstractRefundServiceImpl{

    @Override
    @Transactional
    public RefundBO refund(RefundDTO refundDTO) {

        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        // 支付订单状态判断, 数据问题直接抛异常
        validate(refundDTO);

        // 判断退款状态，创建退款记录
        RefundRecordDO refundRecordDO = createRecordByPaymentDetail(refundDTO);

        // 调用余额系统加钱
        InnerResultBO innerResultBO = balanceManager.modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        // 成功时修改状态提交事务，失败时抛出异常回滚，超时时直接提交事务
        execResult(innerResultBO, paymentDetailDO);

        return new RefundBO(innerResultBO.getStatus());
    }
}
