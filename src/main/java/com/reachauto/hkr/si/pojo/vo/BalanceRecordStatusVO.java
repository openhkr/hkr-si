package com.reachauto.hkr.si.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Deng Fangzhi
 * on 2018/2/1
 */
@Data
public class BalanceRecordStatusVO {

    @ApiModelProperty(value = "余额业务状态，1：未执行，2：已执行，4：已回滚", required = true)
    private Integer status;
}
