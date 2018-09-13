package com.reachauto.hkr.si.pojo.parameter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/1/16.
 */
@Data
@ApiModel
public class PaymentThirdParameter {
    @ApiModelProperty(position = 1, required = true, value = "业务订单号")
    @NotBlank
    @Length(min = 1, max = 64, message = "订单id最长支持64位")
    private String orderId;
    @ApiModelProperty(position = 2, required = true, value = "业务订单类型，1是租车订单,2是充电订单,4 违章缴费, 32预约用车预约费, 64 预约用车租赁费")
    @NotBlank
    @Pattern(regexp = "[1248]|32|64", message = "业务订单类型只包括1,2,4,32,64")
    private String orderType;
    @ApiModelProperty(position = 3, required = true, value = "支付类型，2是支付宝,4是微信,8是微信公众号JSAPI")
    @Pattern(regexp = "[248]", message = "支付类型只包括2,4,8")
    @NotBlank
    private String paymentSource;
    @ApiModelProperty(position = 4, required = true, value = "支付金额，金额最多支持两位小数，最大金额999999.99")
    @DecimalMin(value = "0.01", message = "最小支付0.01元")
    @DecimalMax(value = "999999.99", message = "最大支持支付金额999999.99元")
    @Digits(integer=6, fraction=2)
    @NotNull
    private BigDecimal price;
    @ApiModelProperty(position = 5, required = true, value = "业务回调地址，例如：http://hkr-agg-he/api/v1/pay/callback")
    @NotBlank
    @Length(min = 1, max = 512, message = "回调地址最长支持512位")
    private String callbackUrl;
    @ApiModelProperty(position = 6, required = true, value = "支付者用户名id")
    @NotBlank
    @Length(min = 1, max = 32, message = "支付用户名id")
    private String userId;
}
