package com.reachauto.hkr.si.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2018/1/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayVO {
    @ApiModelProperty(position = 1, required = true, value = "支付流水id")
    private String paymentId;
    @ApiModelProperty(position = 2, required = true, value = "签名后的支付参数")
    private String signedStr;
}
