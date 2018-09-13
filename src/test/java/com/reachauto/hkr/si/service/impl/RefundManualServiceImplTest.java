package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.constant.BalanceRemarkConstants;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.RefundRecordRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
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
 * @create 2018-02-27 10:18
 */

public class RefundManualServiceImplTest {

    @InjectMocks
    private RefundManualServiceImpl refundManualService = new RefundManualServiceImpl();

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Mock
    protected RefundRecordRepository refundRecordRepository;

    @Mock
    protected BalanceManager balanceManager;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void refundManual_ordernotexisit(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(3);
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");
        try{
            refundManualService.refundManual(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_NOTEX);
        }
    }

    @Test
    public void payment_refundManual_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunded(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());
        RefundBO refundBO = refundManualService.refundManual(refundDTO);
        Assert.assertTrue(refundBO.isSuccessed());
    }

    @Test
    public void payment_refundManual_refunding(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");
        Mockito.doReturn(0).when(paymentDetailRepository).manualRefunded(paymentDetailDO);
        try{
            refundManualService.refundManual(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_REFUNDING);
        }
    }

    @Test
    public void recharge_refundManual_success(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("11");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunding(paymentDetailDO);
        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunded(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), BalanceRemarkConstants.RECHARGE_MANUAL_REFUNDS);

        RefundBO refundBO = refundManualService.refundManual(refundDTO);
        Assert.assertTrue(refundBO.isSuccessed());
    }

    @Test
    public void recharge_refundManual_timeout(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("11");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunding(paymentDetailDO);
        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunded(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), BalanceRemarkConstants.RECHARGE_MANUAL_REFUNDS);

        RefundBO refundBO = refundManualService.refundManual(refundDTO);
        Assert.assertTrue(refundBO.isNoResponse());
    }

    @Test
    public void recharge_refundManual_balacnefailed(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("11");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunding(paymentDetailDO);
        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunded(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getFailInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), BalanceRemarkConstants.RECHARGE_MANUAL_REFUNDS);

        try{
            refundManualService.refundManual(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.REFUND_FAILED);
        }
    }

    @Test
    public void recharge_refundManual_createexception(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("11");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(0).when(paymentDetailRepository).manualRefunding(paymentDetailDO);

        InnerResultBO innerResultBO = InnerResultBO.getFailInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), refundDTO.getDesc());

        try{
            refundManualService.refundManual(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_REFUNDING);
        }
    }

    @Test
    public void recharge_refundManual_refundexception(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("11");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(1).when(paymentDetailRepository).manualRefunding(paymentDetailDO);
        Mockito.doThrow(new RuntimeException()).when(paymentDetailRepository).manualRefunded(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), BalanceRemarkConstants.RECHARGE_MANUAL_REFUNDS);

        RefundBO refundBO = refundManualService.refundManual(refundDTO);
        Assert.assertTrue(refundBO.isNoResponse());
    }
}
