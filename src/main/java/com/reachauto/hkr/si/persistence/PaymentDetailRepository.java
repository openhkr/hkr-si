package com.reachauto.hkr.si.persistence;

import com.reachauto.hkr.common.persistence.PagingAndSortingRepository;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */
@Mapper
public interface PaymentDetailRepository extends PagingAndSortingRepository<PaymentDetailDO> {

    /**
     * 根据订单类型和订单号查询支付流水
     */
    List<PaymentDetailDO> findByOrderTypeAndOrderId(@Param("orderType") Integer orderType,
                                                    @Param("orderId") String orderId);

    /**
     * 支付订单更新成结束状态
     * @param paymentDetailDO
     * @return
     */
    int finishTrade(PaymentDetailDO paymentDetailDO);

    /**
     *
     * @param paymentDetailDO
     * @return
     */
    int settlementTrade(PaymentDetailDO paymentDetailDO);

    /**
     * 更新成退款中
     * @param paymentDetailDO
     * @return
     */
    int refunding(PaymentDetailDO paymentDetailDO);

    /**
     * 更新成财务退款中
     * @param paymentDetailDO
     * @return
     */
    int manualRefunding(PaymentDetailDO paymentDetailDO);

    /**
     * 更新成已退款
     * @param paymentDetailDO
     * @return
     */
    int refunded(PaymentDetailDO paymentDetailDO);

    /**
     * 更新成财务已退款
     * @param paymentDetailDO
     * @return
     */
    int manualRefunded(PaymentDetailDO paymentDetailDO);

    /**
     * 退款失败，将退款中置为“支付成功”
     * @param paymentDetailDO
     * @return
     */
    int refundFailed(PaymentDetailDO paymentDetailDO);

    /**
     * 查询未处理完成的支付订单
     * @return
     */
    List<PaymentDetailDO> queryUnPayresult(Integer envTag);

    /**
     * 查询退款中的支付订单
     * @return
     */
    List<PaymentDetailDO> queryRefundingResult(Integer envTag);

    /**
     * 查询未进行业务回调的支付订单
     * @return
     */
    List<PaymentDetailDO> queryUnCallback(Integer envTag);

    /**
     * 根据用户id和类型查询金额总和
     * @param userId
     * @param type
     * @return
     */
    BigDecimal sumfeeByUserAndOrderType(@Param("userId") String userId, @Param("type") int type);
}
