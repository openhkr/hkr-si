package com.reachauto.hkr.si.pojo.parameter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * @author zhang.shuo
 */
@Data
@ApiModel
public class RefundParameter {

    @ApiModelProperty(position = 1, required = true, value = "支付订单ID")
    @NotBlank
    @Length(min = 1, max = 64, message = "订单id最长支持64位")
    private String paymentId;

    @ApiModelProperty(position = 2, required = true, value = "退款金额，退款金额必须小于等于支付金额")
    @DecimalMin(value = "0.01", message = "金额最小0.01元")
    @DecimalMax(value = "999999.99", message = "金额最大999999.99元")
    @Digits(integer=6, fraction=2)
    private BigDecimal amount;

    @ApiModelProperty(position = 3, value = "退款备注，非必填")
    @Length(max = 50, message = "退款备注最多支持50个字符")
    private String desc;
}
