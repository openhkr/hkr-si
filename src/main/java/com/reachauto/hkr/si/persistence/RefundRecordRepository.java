package com.reachauto.hkr.si.persistence;

import com.reachauto.hkr.common.persistence.PagingAndSortingRepository;
import com.reachauto.hkr.si.entity.RefundRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhang.shuo
 */
@Mapper
public interface RefundRecordRepository extends PagingAndSortingRepository<RefundRecordDO> {

    /**
     * 根据支付订单ID查询退款记录
     * @param paymentId
     * @return
     */
    RefundRecordDO findByPaymentId(Long paymentId);
}