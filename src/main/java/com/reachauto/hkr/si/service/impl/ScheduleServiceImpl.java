package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.cache.RedisLock;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.WeChatManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.BizCallbackService;
import com.reachauto.hkr.si.service.PayQueryService;
import com.reachauto.hkr.si.service.RefundConfirmService;
import com.reachauto.hkr.si.service.ScheduleService;
import com.reachauto.hkr.si.utils.ApplicationContextTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangshuo
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    /**
     * 10天的毫秒数
     */
    private static final long DAY_SECONDS_15 = 1000L * 60L * 60L * 24L * 15L;

    private static final String SYCNPAYRESULT_LOCK_KEY = "sycn_payresult";

    private static final String SYCNREFUNDRESULT_LOCK_KEY = "sycn_refundresult";

    private static final String RETRYCALLBACK_LOCK_KEY = "retry_callback";

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @Autowired
    private WeChatManager weChatManager;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private BizCallbackService bizCallbackService;

    @Autowired
    private RefundConfirmService refundConfirmService;

    @Override
    public void syncPayResult(Integer envTag) {
        if (!redisLock.lock(SYCNPAYRESULT_LOCK_KEY + envTag)) {
            return;
        }
        try {
            List<PaymentDetailDO> list = paymentDetailRepository.queryUnPayresult(envTag);
            for (PaymentDetailDO paymentDetailDO : list) {
                try {
                    PaymentSourceEnum paymentSource = PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource());
                    PayQueryService payQueryService = (PayQueryService) ApplicationContextTool.get(paymentSource.getQueryClazz());
                    PaymentDetailDO result = payQueryService.orderQueryFull(paymentDetailDO);
                    /**
                     * 如果不是结束状态，判断时间
                     * 支付宝15天以上订单直接关闭
                     * 微信发起关闭订单，等待下一次查询同步关闭状态
                     */
                    close15daysAgoOrder(paymentSource, paymentDetailDO, result);
                } catch (Exception ex) {
                    log.error("同步支付结果信息出错" + paymentDetailDO.getPaymentId(), ex);
                }
            }
        } finally {
            redisLock.unlock(SYCNPAYRESULT_LOCK_KEY + envTag);
        }
    }

    /**
     * 对退款中的退款流水，重新调用三方调用（或余额）执行退款，
     *      如果返回成功，将支付流水置为已退款
     *      如果返回失败，将支付流水置为支付成功，将退款流水状态置为失败
     *      如果超时，不做处理等待下次轮询
     * @param envTag 环境变量
     */
    @Override
    public void syncRefundResult(Integer envTag) {
        if (!redisLock.lock(SYCNREFUNDRESULT_LOCK_KEY + envTag)) {
            return;
        }
        try {
            List<PaymentDetailDO> list = paymentDetailRepository.queryRefundingResult(envTag);
            for (PaymentDetailDO paymentDetailDO : list) {
                try {
                    refundConfirmService.confirm(paymentDetailDO);
                } catch (Exception ex) {
                    log.error("同步退款结果信息出错" + paymentDetailDO.getPaymentId(), ex);
                }
            }
        } finally {
            redisLock.unlock(SYCNREFUNDRESULT_LOCK_KEY + envTag);
        }
    }

    @Override
    public void retryCallback(Integer envTag) {
        if (!redisLock.lock(RETRYCALLBACK_LOCK_KEY + envTag)) {
            return;
        }
        try {
            List<PaymentDetailDO> list = paymentDetailRepository.queryUnCallback(envTag);
            for (PaymentDetailDO paymentDetailDO : list) {
                try {
                    // 执行业务回调并更改状态
                    bizCallbackService.bizCallBack(paymentDetailDO);
                } catch (Exception ex) {
                    log.error("回调业务方定时任务出错" + paymentDetailDO.getPaymentId(), ex);
                }
            }
        } finally {
            redisLock.unlock(RETRYCALLBACK_LOCK_KEY + envTag);
        }
    }

    /**
     * 根据订单状态判断，满足条件的，关闭15天以前的订单
     * @param paymentSource
     * @param detail
     * @param result
     */
    private void close15daysAgoOrder(PaymentSourceEnum paymentSource, PaymentDetailDO detail, PaymentDetailDO result){
        if (!result.hasFinishPayed() && System.currentTimeMillis() - result.getCreatedAt().getTime() > DAY_SECONDS_15) {
            if (PaymentSourceEnum.ALI.equals(paymentSource)) {
                // 支付宝支付直接关闭
                result.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
                paymentDetailRepository.finishTrade(result);
            } else {
                weChatManager.weChatCloseOrder(paymentSource, detail.getPaymentId());
            }
        }
    }
}
