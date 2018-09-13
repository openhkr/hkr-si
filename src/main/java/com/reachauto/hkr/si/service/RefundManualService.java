package com.reachauto.hkr.si.service;

import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;

/**
 * @author zhangshuo
 */
public interface RefundManualService {

    /**
     * 财务人工退款接口
     * @param refundDTO
     * @return
     */
    RefundBO refundManual(RefundDTO refundDTO);
}
