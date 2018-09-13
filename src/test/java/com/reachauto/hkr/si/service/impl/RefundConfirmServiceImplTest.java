package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.entity.RefundRecordDO;
import com.reachauto.hkr.si.manager.AliPayManager;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.manager.WeChatManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.RefundRecordRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-26 14:44
 */

public class RefundConfirmServiceImplTest {

    @InjectMocks
    private RefundConfirmServiceImpl refundConfirmServiceImpl = new RefundConfirmServiceImpl();

    @Mock
    private AliPayManager aliPayManager;

    @Mock
    private WeChatManager weChatManager;

    @Mock
    private BalanceManager balanceManager;

    @Mock
    private RefundRecordRepository refundRecordRepository;

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void aLiPayRetryRefund_confirm_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(aliPayManager).refund(String.valueOf(refundRecordDO.getPaymentId()),
                refundRecordDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                String.valueOf(refundRecordDO.getAmount()),
                refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void aLiPayRetryRefund_confirm_failed(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(null).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(aliPayManager).refund(String.valueOf(refundRecordDO.getPaymentId()),
                refundRecordDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                String.valueOf(refundRecordDO.getAmount()),
                refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refundFailed(paymentDetailDO);
        Mockito.doReturn(1).when(refundRecordRepository).delete(new Long(1L));
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void aLiRechargeRetryRefund_confirm_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("12");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(aliPayManager).refund(String.valueOf(refundRecordDO.getPaymentId()),
                refundRecordDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                String.valueOf(refundRecordDO.getAmount()),
                refundRecordDO.getRemarks());

        InnerResultBO balanceResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundRecordDO.getAmount().negate(), refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void aLiRechargeRetryRefund_confirm_failed(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("112");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(aliPayManager).refund(String.valueOf(refundRecordDO.getPaymentId()),
                refundRecordDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                String.valueOf(refundRecordDO.getAmount()),
                refundRecordDO.getRemarks());

        InnerResultBO balanceResultBO = InnerResultBO.getFailInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundRecordDO.getAmount().negate(), refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refundFailed(paymentDetailDO);
        Mockito.doReturn(1).when(refundRecordRepository).delete(new Long(1L));
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }


    @Test
    public void weChatPayRetryRefund_confirm_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.WECHAT.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(weChatManager).weChatRefund(PaymentSourceEnum.WECHAT,
                String.valueOf(refundRecordDO.getPaymentId()),
                paymentDetailDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getTradeTotalFee(),
                refundRecordDO.getAmount(),
                refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void weChatRechargeRetryRefund_confirm_timeout(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("112");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.WECHAT.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(weChatManager).weChatRefund(PaymentSourceEnum.WECHAT,
                String.valueOf(refundRecordDO.getPaymentId()),
                paymentDetailDO.getTradeNo(),
                String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getTradeTotalFee(),
                refundRecordDO.getAmount(),
                refundRecordDO.getRemarks());

        InnerResultBO balanceResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundRecordDO.getAmount().negate(), refundRecordDO.getRemarks());

        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void balancePayRetryRefund_confirm_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.COMMON_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundRecordDO.getAmount(), refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void balancePayRetryRefund_confirm_failed(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test1");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.COMMON_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO innerResultBO = InnerResultBO.getFailInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundRecordDO.getAmount(), refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refundFailed(paymentDetailDO);
        Mockito.doReturn(1).when(refundRecordRepository).delete(new Long(1L));
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void retryManualRefund_confirm_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_MANUAL_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO balanceResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundRecordDO.getAmount().negate(), refundRecordDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void retryManualRefund_confirm_timeout(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("11");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_MANUAL_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        InnerResultBO balanceResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance(String.valueOf(refundRecordDO.getId()),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundRecordDO.getAmount().negate(), refundRecordDO.getRemarks());

        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }

    @Test
    public void retryManualRefund_confirm_typeerror(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("11");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_MANUAL_REFUNDING.getCode());

        RefundRecordDO refundRecordDO = new RefundRecordDO();
        refundRecordDO.setTradeNo("11");
        refundRecordDO.setId(1L);
        refundRecordDO.setAmount(BigDecimal.valueOf(1));
        refundRecordDO.setRemarks("1");

        Mockito.doReturn(refundRecordDO).when(refundRecordRepository).findByPaymentId(Long.valueOf(paymentDetailDO.getPaymentId()));

        refundConfirmServiceImpl.confirm(paymentDetailDO);
    }
}
