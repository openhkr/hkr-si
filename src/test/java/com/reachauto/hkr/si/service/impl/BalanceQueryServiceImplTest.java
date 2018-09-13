package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.constant.BalanceRecordStatusConstants;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.manager.WeChatManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.BizCallbackService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-28 9:13
 */

public class BalanceQueryServiceImplTest {

    @InjectMocks
    private BalanceQueryServiceImpl payQueryService = new BalanceQueryServiceImpl();

    @Mock
    protected PaymentDetailRepository paymentDetailRepository;

    @Mock
    protected BizCallbackService bizCallbackService;

    @Mock
    protected BalanceManager balanceManager;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(bizCallbackService).bizCallBack(Mockito.any());
    }

    @Test
    public void finished_orderQueryFull_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.COMMON_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);

        PaymentDetailDO ret = payQueryService.orderQueryFull("1");
        Assert.assertEquals(ret.getTradeStatus(), TradeStatusEnum.TRADE_SUCCESS.getCode());
    }

    @Test
    public void processing_orderQueryFull_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setBuyerId("1111");
        paymentDetailDO.setTradeNo("111");
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.COMMON_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);

        Mockito.doReturn(BalanceRecordStatusConstants.BALANCE_RECORD_STATUS_SUCCESS).when(balanceManager).recordStatusQuery(String.valueOf(paymentDetailDO.getPaymentId()));
        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).rollbackModifyRecord(String.valueOf(paymentDetailDO.getPaymentId()));

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(paymentDetailDO);

        PaymentDetailDO ret = payQueryService.orderQueryFull("1");
        Assert.assertEquals(ret.getTradeStatus(), TradeStatusEnum.TRADE_FAIL.getCode());
    }
}
