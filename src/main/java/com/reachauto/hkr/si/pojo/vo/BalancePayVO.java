package com.reachauto.hkr.si.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/2/9.
 */
@Data
public class BalancePayVO {
    @ApiModelProperty(position = 1, required = true, value = "支付流水id")
    private String paymentId;
    @ApiModelProperty(position = 2, required = true, value = "支付金额")
    private BigDecimal amount;
}
