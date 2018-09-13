package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.entity.RefundRecordDO;
import com.reachauto.hkr.si.manager.WeChatManager;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhang.shuo
 */
@Slf4j
@Service
public class WeChatRefundServiceImpl extends AbstractRefundServiceImpl {

    @Autowired
    private WeChatManager weChatManager;

    @Override
    @Transactional
    public RefundBO refund(RefundDTO refundDTO) {

        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        // 支付订单状态判断, 数据问题直接抛异常
        AbstractRefundServiceImpl.validate(refundDTO);

        // 判断退款状态，选择合适的退款记录
        RefundRecordDO refundRecordDO = createRecordByPaymentDetail(refundDTO);

        // 如果是充值退款，减余额处理, 接口内判断, 失败抛出异常，超时直接返回
        InnerResultBO balanceResultBO = execBalance(String.valueOf(refundRecordDO.getId()), refundDTO, paymentDetailDO);
        if(balanceResultBO.isTimeOut()){
            return RefundBO.getNoResponseInstants();
        }

        InnerResultBO innerResultBO = weChatManager.weChatRefund(PaymentSourceEnum.WECHAT,
                refundDTO.getPaymentDetailDO().getPaymentId(),
                refundDTO.getPaymentDetailDO().getTradeNo(),
                String.valueOf(refundRecordDO.getId()), paymentDetailDO.getTradeTotalFee(),
                refundRecordDO.getAmount(), refundDTO.getDesc());

        // 成功时修改状态提交事务，失败时抛出异常回滚，超时时直接提交事务
        execResult(innerResultBO, paymentDetailDO);

        return new RefundBO(innerResultBO.getStatus());
    }
}
