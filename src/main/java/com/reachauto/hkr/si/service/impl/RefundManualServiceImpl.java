package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.constant.BalanceRemarkConstants;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.entity.RefundRecordDO;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.RefundRecordRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.service.RefundManualService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhangshuo
 */
@Slf4j
@Service
public class RefundManualServiceImpl implements RefundManualService {

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @Autowired
    protected RefundRecordRepository refundRecordRepository;

    @Autowired
    protected BalanceManager balanceManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public RefundBO refundManual(RefundDTO refundDTO) {

        // 数据验证
        AbstractRefundServiceImpl.validate(refundDTO);

        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        if (PaymentTypeEnum.PAYMENT.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
            // 如果是支付订单
            // 判断退款状态，创建退款记录
            RefundRecordDO refundRecordDO = null;
            // 更新成退款中，并插入数据
            if (paymentDetailRepository.manualRefunded(paymentDetailDO) > 0) {
                // 创建退款流水
                refundRecordDO = new RefundRecordDO(refundDTO);
                refundRecordRepository.create(refundRecordDO);
                return RefundBO.getSuccessInstants();
            } else {
                throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUNDING, "order.refunding");
            }
        } else if (PaymentTypeEnum.RECHARGE.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
            // 如果是充值订单
            // 判断退款状态，创建退款记录
            RefundRecordDO refundRecordDO = null;
            // 更新成退款中，并插入数据
            if (paymentDetailRepository.manualRefunding(paymentDetailDO) > 0) {
                // 创建退款流水
                refundRecordDO = new RefundRecordDO(refundDTO);
                refundRecordRepository.create(refundRecordDO);
            } else {
                throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUNDING, "order.refunding");
            }
            // 调用余额接口减钱
            InnerResultBO innerResultBO = balanceManager.modifyBalance(String.valueOf(refundRecordDO.getId()),
                    paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                    refundDTO.getAmount().negate(), BalanceRemarkConstants.RECHARGE_MANUAL_REFUNDS);

            // 成功时修改状态提交事务，失败时抛出异常回滚，超时直接提交事务
            if (innerResultBO.isSuccessed()) {
                try {
                    paymentDetailRepository.manualRefunded(paymentDetailDO);
                    return RefundBO.getSuccessInstants();
                } catch (Exception e) {
                    // 此处异常不应该回滚事务
                    log.error("退款更新支付状态出现异常", e);
                }
            } else if (innerResultBO.isFailed()) {
                throw new HkrBusinessException(ErrCodeConstant.REFUND_FAILED, "refund.failed");
            }
            return RefundBO.getNoResponseInstants();
        } else {
            // 订单不存在
            throw new HkrBusinessException(ErrCodeConstant.ORDER_NOTEX, "order.notex");
        }
    }
}
