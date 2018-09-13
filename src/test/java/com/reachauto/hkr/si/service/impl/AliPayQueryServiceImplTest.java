package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.AliPayManager;
import com.reachauto.hkr.si.manager.BalanceManager;
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

public class AliPayQueryServiceImplTest {

    @InjectMocks
    private AliPayQueryServiceImpl payQueryService = new AliPayQueryServiceImpl();

    @Mock
    protected PaymentDetailRepository paymentDetailRepository;

    @Mock
    protected BizCallbackService bizCallbackService;

    @Mock
    protected BalanceManager balanceManager;

    @Mock
    private AliPayManager aliPayManager;

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
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
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
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);

        PaymentQueryBO paymentQueryBO = PaymentQueryBO.getSuccessInstance("1", "1");
        paymentQueryBO.setBuyerId("1111");
        paymentQueryBO.setTradeTotalFee("100");
        paymentQueryBO.setTradeNo("111");
        Mockito.doReturn(paymentQueryBO).when(aliPayManager).query(paymentDetailDO.getPaymentId(), "");

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(paymentDetailDO);

        PaymentDetailDO ret = payQueryService.orderQueryFull("1");
        Assert.assertEquals(ret.getTradeStatus(), TradeStatusEnum.TRADE_SUCCESS.getCode());
    }

    @Test
    public void processing_orderQueryFull_failed(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setBuyerId("1111");
        paymentDetailDO.setTradeNo("111");
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);

        PaymentQueryBO paymentQueryBO = PaymentQueryBO.getFailInstance("1", "1");
        paymentQueryBO.setBuyerId("1111");
        paymentQueryBO.setTradeTotalFee("100");
        paymentQueryBO.setTradeNo("111");
        Mockito.doReturn(paymentQueryBO).when(aliPayManager).query(paymentDetailDO.getPaymentId(), "");

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(paymentDetailDO);

        PaymentDetailDO ret = payQueryService.orderQueryFull("1");
        Assert.assertEquals(ret.getTradeStatus(), TradeStatusEnum.TRADE_FAIL.getCode());
    }

    @Test
    public void processing_orderQueryFull_timeout(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setBuyerId("1111");
        paymentDetailDO.setTradeNo("111");
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);

        PaymentQueryBO paymentQueryBO = PaymentQueryBO.getPayingInstance("1", "1");
        paymentQueryBO.setBuyerId("1111");
        paymentQueryBO.setTradeTotalFee("100");
        paymentQueryBO.setTradeNo("111");
        Mockito.doReturn(paymentQueryBO).when(aliPayManager).query(paymentDetailDO.getPaymentId(), "");

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(paymentDetailDO);

        PaymentDetailDO ret = payQueryService.orderQueryFull("1");
        Assert.assertEquals(ret.getTradeStatus(), TradeStatusEnum.TRADE_PROCESSING.getCode());
    }

    @Test
    public void processingrecharge_orderQueryFull_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setBuyerId("1111");
        paymentDetailDO.setTradeNo("111");
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);

        PaymentQueryBO paymentQueryBO = PaymentQueryBO.getSuccessInstance("1", "1");
        paymentQueryBO.setBuyerId("1111");
        paymentQueryBO.setTradeTotalFee("100");
        paymentQueryBO.setTradeNo("111");
        Mockito.doReturn(paymentQueryBO).when(aliPayManager).query(paymentDetailDO.getPaymentId(), "");

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance(String.valueOf(paymentDetailDO.getPaymentId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                paymentDetailDO.getTradeTotalFee(), paymentDetailDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(paymentDetailDO);

        PaymentDetailDO ret = payQueryService.orderQueryFull("1");
        Assert.assertEquals(ret.getTradeStatus(), TradeStatusEnum.TRADE_SUCCESS.getCode());
    }

    @Test
    public void processingrecharge_orderQueryFull_failed(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setBuyerId("1111");
        paymentDetailDO.setTradeNo("111");
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);

        PaymentQueryBO paymentQueryBO = PaymentQueryBO.getSuccessInstance("1", "1");
        paymentQueryBO.setBuyerId("1111");
        paymentQueryBO.setTradeTotalFee("100");
        paymentQueryBO.setTradeNo("111");
        Mockito.doReturn(paymentQueryBO).when(aliPayManager).query(paymentDetailDO.getPaymentId(), "");

        InnerResultBO innerResultBO = InnerResultBO.getFailInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance(String.valueOf(paymentDetailDO.getPaymentId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                paymentDetailDO.getTradeTotalFee(), paymentDetailDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(paymentDetailDO);

        PaymentDetailDO ret = payQueryService.orderQueryFull("1");
        Assert.assertEquals(ret.getTradeStatus(), TradeStatusEnum.TRADE_PROCESSING.getCode());
    }
}
