package com.reachauto.hkr.si.pojo.parameter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/2/9.
 */
@Data
@ApiModel
public class PaymentBalanceParameter {
    @ApiModelProperty(position = 1, required = true, value = "业务订单号")
    @NotBlank
    @Length(min = 1, max = 64, message = "订单id最长支持64位")
    private String orderId;
    @ApiModelProperty(position = 2, required = true, value = "业务订单类型，1是租车订单,2是充电订单,4 违章缴费, 32预约用车预约费, 64 预约用车租赁费，128 保证金抵扣")
    @NotBlank
    @Pattern(regexp = "[1248]|32|64|128", message = "业务订单类型只包括1,2,4,32,64,128")
    private String orderType;
    @ApiModelProperty(position = 3, required = true, value = "支付类型，1是普通余额,16是保证金余额")
    @Pattern(regexp = "[1]|16", message = "支付类型只包括1,16")
    @NotBlank
    private String paymentSource;
    @ApiModelProperty(position = 4, required = true, value = "支付金额，金额最多支持两位小数，最大金额999999.99")
    @DecimalMin(value = "0.01", message = "最小支付0.01元")
    @DecimalMax(value = "999999.99", message = "最大支持支付金额999999.99元")
    @Digits(integer=6, fraction=2)
    @NotNull
    private BigDecimal price;
    @ApiModelProperty(position = 6, required = true, value = "支付者用户名id")
    @NotBlank
    @Length(min = 1, max = 32, message = "支付用户名id")
    private String userId;
}
