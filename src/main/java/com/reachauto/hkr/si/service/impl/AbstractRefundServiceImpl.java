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
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.RefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhang.shuo
 */
@Slf4j
public abstract class AbstractRefundServiceImpl implements RefundService {

    @Autowired
    protected PaymentDetailRepository paymentDetailRepository;

    @Autowired
    protected RefundRecordRepository refundRecordRepository;

    @Autowired
    protected BalanceManager balanceManager;

    /**
     * 根据支付流水创建退款记录，注意基类调用时事务是否正常传递。
     *
     * @param refundDTO
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected RefundRecordDO createRecordByPaymentDetail(RefundDTO refundDTO) {

        // 判断退款状态，创建退款记录
        RefundRecordDO refundRecordDO = null;
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        // 更新成退款中，并插入数据
        if (paymentDetailRepository.refunding(paymentDetailDO) > 0) {
            // 创建退款流水
            refundRecordDO = new RefundRecordDO();
            refundRecordDO.setPaymentId(Long.parseLong(paymentDetailDO.getPaymentId()));
            refundRecordDO.setOrderId(paymentDetailDO.getOrderId());
            refundRecordDO.setOrderType(paymentDetailDO.getOrderType());
            refundRecordDO.setTradeNo(paymentDetailDO.getTradeNo());
            refundRecordDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
            refundRecordDO.setAmount(refundDTO.getAmount());
            refundRecordDO.setRemarks(refundDTO.getDesc());
            refundRecordRepository.create(refundRecordDO);
        } else {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUNDING, "order.refunding");
        }

        return refundRecordDO;
    }

    /**
     * 如果是充值退款，减余额处理, 首先判断是不是充值，如果是，处理余额
     *
     * @param requestId
     * @param refundDTO
     * @param paymentDetailDO
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected InnerResultBO execBalance(String requestId, RefundDTO refundDTO, PaymentDetailDO paymentDetailDO) {
        // 不是充值退款直接返回成功
        if (!PaymentTypeEnum.RECHARGE.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
            return InnerResultBO.getSuccessInstants();
        }
        // 如果是充值退款，减余额处理
        InnerResultBO balanceResultBO = balanceManager.modifyBalance(requestId,
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), BalanceRemarkConstants.RECHARGE_REFUNDS);
        if (balanceResultBO.isTimeOut()) {
            // 超时直接提交事务返回
            return InnerResultBO.getTimeOutInstants();
        } else if (balanceResultBO.isFailed()) {
            // 失败抛异常回滚
            throw new HkrBusinessException(ErrCodeConstant.REFUND_FAILED, "refund.failed");
        }
        return InnerResultBO.getSuccessInstants();
    }

    /**
     * 处理退款结果
     *
     * @param innerResultBO
     * @param paymentDetailDO
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected void execResult(InnerResultBO innerResultBO, PaymentDetailDO paymentDetailDO) {
        // 成功时修改状态提交事务，失败时抛出异常回滚，超时直接提交事务
        if (innerResultBO.isSuccessed()) {
            try {
                paymentDetailRepository.refunded(paymentDetailDO);
            } catch (Exception e) {
                // 此处异常不应该回滚事务
                log.error("退款更新支付状态出现异常", e);
            }
        } else if (innerResultBO.isFailed()) {
            /**
             * 此处如果是充值退款，返回退款中，不能抛异常回滚，并且置状态为退款中，防止未支付的订单可以多次退款。
             * 如果是支付退款，直接抛异常回滚
             */
            if (!PaymentTypeEnum.RECHARGE.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
                throw new HkrBusinessException(ErrCodeConstant.REFUND_FAILED, "refund.failed");
            }
            innerResultBO.setToTimeOut();
        }
    }

    /**
     * 验证数据正确性
     *
     * @param refundDTO
     */
    public static void validate(RefundDTO refundDTO) {

        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        if (!TradeStatusEnum.TRADE_SUCCESS.getCode().equals(paymentDetailDO.getTradeStatus())) {

            // 已退款的
            if (TradeStatusEnum.TRADE_REFUNDED.getCode().equals(paymentDetailDO.getTradeStatus())
                    || TradeStatusEnum.TRADE_MANUAL_REFUNDED.getCode().equals(paymentDetailDO.getTradeStatus())) {
                throw new HkrBusinessException(ErrCodeConstant.ORDER_HAS_REF, "order.has.ref");
            }
            // 退款中的
            if (TradeStatusEnum.TRADE_REFUNDING.getCode().equals(paymentDetailDO.getTradeStatus())
                    || TradeStatusEnum.TRADE_MANUAL_REFUNDING.getCode().equals(paymentDetailDO.getTradeStatus())) {
                throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUNDING, "order.refunding");
            }
            // 其他的
            throw new HkrBusinessException(ErrCodeConstant.ORDER_CANNOT_REFUND, "order.cannot.refund");
        }
        if (refundDTO.getAmount().compareTo(paymentDetailDO.getTradeTotalFee()) > 0) {
            throw new HkrBusinessException(ErrCodeConstant.AMOUNT_ERROR, "amount.error");
        }
    }
}
