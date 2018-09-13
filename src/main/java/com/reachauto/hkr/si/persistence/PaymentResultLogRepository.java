package com.reachauto.hkr.si.persistence;

import com.reachauto.hkr.common.persistence.PagingAndSortingRepository;
import com.reachauto.hkr.si.entity.PaymentResultLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator on 2018/1/15.
 */
@Mapper
public interface PaymentResultLogRepository extends PagingAndSortingRepository<PaymentResultLogDO> {
}
