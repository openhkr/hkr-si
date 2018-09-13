package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.cache.RedisLock;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.WeChatManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.service.BizCallbackService;
import com.reachauto.hkr.si.service.RefundConfirmService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-27 9:15
 */

public class ScheduleServiceImplTest {

    @InjectMocks
    private ScheduleServiceImpl scheduleService = new ScheduleServiceImpl();

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Mock
    private WeChatManager weChatManager;

    @Mock
    private RedisLock redisLock;

    @Mock
    private BizCallbackService bizCallbackService;

    @Mock
    private RefundConfirmService refundConfirmService;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(redisLock).unlock(Mockito.any());
    }

    @Test
    public void syncPayResult_success(){
        Mockito.doReturn(true).when(redisLock).lock("sycn_payresult1");
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);
        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).queryUnPayresult(1);
        scheduleService.syncPayResult(1);
    }

    @Test
    public void syncPayResult_locked(){
        Mockito.doReturn(false).when(redisLock).lock("sycn_payresult1");
        scheduleService.syncPayResult(1);
    }

    @Test
    public void syncRefundResult_success(){
        Mockito.doReturn(true).when(redisLock).lock("sycn_refundresult1");
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);
        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).queryRefundingResult(1);
        Mockito.doNothing().when(refundConfirmService).confirm(Mockito.any());
        scheduleService.syncRefundResult(1);
    }

    @Test
    public void syncRefundResult_exception(){
        Mockito.doReturn(true).when(redisLock).lock("sycn_refundresult1");
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);
        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).queryRefundingResult(1);
        Mockito.doThrow(new RuntimeException()).when(refundConfirmService).confirm(Mockito.any());
        scheduleService.syncRefundResult(1);
    }

    @Test
    public void syncRefundResult_locked(){
        Mockito.doReturn(false).when(redisLock).lock("sycn_refundresult1");
        scheduleService.syncRefundResult(1);
    }

    @Test
    public void retryCallback_success(){
        Mockito.doReturn(true).when(redisLock).lock("retry_callback1");
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);
        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).queryUnCallback(1);
        Mockito.doNothing().when(bizCallbackService).bizCallBack(Mockito.any());
        scheduleService.retryCallback(1);
    }

    @Test
    public void retryCallback_exception(){
        Mockito.doReturn(true).when(redisLock).lock("retry_callback1");
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);
        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).queryUnCallback(1);
        Mockito.doThrow(new RuntimeException()).when(bizCallbackService).bizCallBack(Mockito.any());
        scheduleService.retryCallback(1);
    }

    @Test
    public void retryCallback_locked(){
        Mockito.doReturn(false).when(redisLock).lock("retry_callback1");
        scheduleService.retryCallback(1);
    }
}
