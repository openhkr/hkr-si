package com.reachauto.hkr.si.pojo.dto;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhang.shuo
 */
@Data
@AllArgsConstructor
public class RefundDTO {
    /**
     * 退款交易流水ID
     */
    private PaymentDetailDO paymentDetailDO;
    /**
     * 退款金额
     */
    private BigDecimal amount;
    /**
     * 退款描述
     */
    private String desc;
}
