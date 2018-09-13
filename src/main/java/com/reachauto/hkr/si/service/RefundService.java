package com.reachauto.hkr.si.service;

import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;

/**
 * 退款接口
 */
public interface RefundService {

    /**
     * 退款接口
     * @param refundDTO
     * @return
     */
    RefundBO refund(RefundDTO refundDTO);
}