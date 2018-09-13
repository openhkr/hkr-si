package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.dto.PayParaDTO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.pojo.vo.PayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author zhangshuo
 */
@Slf4j
@Service
public class BalancePayServiceImpl extends AbstractPayServiceImpl {

    @Autowired
    private BalanceManager balanceManager;

    /**
     * 余额支付的要素：用户，支付类型，支付金额
     * @param payParaDTO
     * @return
     */
    @Override
    public PayVO pay(PayParaDTO payParaDTO) {
        // 查询同类型同单号订单
        List<PaymentDetailDO> paymentDetailDOList = paymentDetailRepository.findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        /**
         * 1.有支付完成的，就直接返回支付成功
         * 2.有结算中的，返回进行中
         * 3.其他情况创建新流水
         */
        if (!ObjectUtils.isEmpty(paymentDetailDOList)) {
            for (PaymentDetailDO paymentDetailDO : paymentDetailDOList) {
                // 验证状态，不符合直接抛异常
                tradeSuccess(paymentDetailDO);
                tradeSettlement(paymentDetailDO);
            }
        }
        log.info("支付信息{}验证通过。", payParaDTO.toString());
        PaymentDetailDO paymentDetailDO = createPaymentDetail(payParaDTO);

        InnerResultBO innerResultBO = InnerResultBO.getFailInstants();
        try{
            innerResultBO = balanceManager.modifyBalance(String.valueOf(paymentDetailDO.getPaymentId()), paymentDetailDO.getUserId(),
                    PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                    paymentDetailDO.getTradeTotalFee().negate(), paymentDetailDO.getRemarks());
        }catch (HkrBusinessException ex){
            if(ex.getCode() == ErrCodeConstant.BALANCE_NOT_ENOUGH){
                // 余额不足当失败处理
                innerResultBO = InnerResultBO.getFailInstants();
            }
        }

        if(innerResultBO.isSuccessed()){
            paymentDetailDO.setTradeNo("");
            paymentDetailDO.setBuyerId(paymentDetailDO.getUserId());
            paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
            //更新payment_detail表
            paymentDetailRepository.finishTrade(paymentDetailDO);
            // 交易成功返回
            return new PayVO(String.valueOf(paymentDetailDO.getPaymentId()), "");
        }else if(innerResultBO.isFailed()){
            paymentDetailDO.setTradeNo("");
            paymentDetailDO.setBuyerId(paymentDetailDO.getUserId());
            paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
            //更新payment_detail表
            paymentDetailRepository.finishTrade(paymentDetailDO);
            // 余额不足
            throw new HkrBusinessException(ErrCodeConstant.BALANCE_NOT_ENOUGH, "balance.not.enough");
        }
        // 支付失败
        throw new HkrBusinessException(ErrCodeConstant.PAY_ERROR, "pay.error");
    }

    @Override
    protected PayVO pay(PaymentDetailDO paymentDetailDO) {
        return null;
    }
}
