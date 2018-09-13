package com.reachauto.hkr.si.pojo.parameter;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangshuo
 */
@Data
public class AccountModifyParameter {

    /**
     * 余额类型：1.普通余额；2.保证金余额
     */
    private String balanceType;

    /**
     * 防重ID
     */
    private String uuid;

    private BigDecimal amount;

    private String remarks;
}
