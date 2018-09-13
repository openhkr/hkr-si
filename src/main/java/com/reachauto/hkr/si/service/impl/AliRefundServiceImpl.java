package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.entity.RefundRecordDO;
import com.reachauto.hkr.si.manager.AliPayManager;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhang.shuo
 */
@Slf4j
@Service
public class AliRefundServiceImpl extends AbstractRefundServiceImpl{

    @Autowired
    private AliPayManager aliPayManager;

    @Override
    @Transactional
    public RefundBO refund(RefundDTO refundDTO) {

        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        // 支付订单状态判断
        AbstractRefundServiceImpl.validate(refundDTO);

        // 根据支付流水创建退款记录
        RefundRecordDO refundRecordDO = createRecordByPaymentDetail(refundDTO);

        // 如果是充值退款，减余额处理, 接口内判断, 失败抛出异常，超时直接返回
        InnerResultBO balanceResultBO = execBalance(String.valueOf(refundRecordDO.getId()), refundDTO, paymentDetailDO);
        if(balanceResultBO.isTimeOut()){
            return RefundBO.getNoResponseInstants();
        }

        InnerResultBO innerResultBO = aliPayManager.refund(paymentDetailDO.getPaymentId(), paymentDetailDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()), refundRecordDO.getAmount().toString(), refundDTO.getDesc());

        // 成功时修改状态提交事务，失败时抛出异常回滚，超时时直接提交事务
        execResult(innerResultBO, paymentDetailDO);

        return new RefundBO(innerResultBO.getStatus());
    }
}
