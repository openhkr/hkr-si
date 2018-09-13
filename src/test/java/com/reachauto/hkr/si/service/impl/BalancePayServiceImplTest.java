package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.cache.PaymentIdGenerator;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.mathcer.ObjectEqualsMathcer;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.dto.PayParaDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.pojo.vo.PayVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-28 10:18
 */

public class BalancePayServiceImplTest {

    @InjectMocks
    private BalancePayServiceImpl payService = new BalancePayServiceImpl();

    @Mock
    protected PaymentDetailRepository paymentDetailRepository;

    @Mock
    private BalanceManager balanceManager;

    @Mock
    private PaymentIdGenerator paymentIdGenerator;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn("11111111").when(paymentIdGenerator).getAPaymentId();
    }

    @Test
    public void pay_success(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setPaymentSource(PaymentSourceEnum.DEPOSIT_BALANCE.getCode());
        payParaDTO.setUserId("1234");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.DEPOSIT_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance(
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getUserId())),
                Mockito.argThat(new ObjectEqualsMathcer<>(PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType())),
                Mockito.argThat(new ObjectEqualsMathcer<>(BigDecimal.valueOf(-100))),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getRemarks())));

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(Mockito.any());

        PayVO payVO = payService.pay(payParaDTO);
        Assert.assertNotNull(payVO.getPaymentId());
    }

    @Test
    public void pay_failed(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setPaymentSource(PaymentSourceEnum.DEPOSIT_BALANCE.getCode());
        payParaDTO.setUserId("1234");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.DEPOSIT_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getFailInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance(
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getUserId())),
                Mockito.argThat(new ObjectEqualsMathcer<>(PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType())),
                Mockito.argThat(new ObjectEqualsMathcer<>(BigDecimal.valueOf(-100))),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getRemarks())));

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(Mockito.any());

        try{
            PayVO payVO = payService.pay(payParaDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.BALANCE_NOT_ENOUGH);
        }
    }

    @Test
    public void pay_timeout(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setPaymentSource(PaymentSourceEnum.DEPOSIT_BALANCE.getCode());
        payParaDTO.setUserId("1234");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.DEPOSIT_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance(
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getUserId())),
                Mockito.argThat(new ObjectEqualsMathcer<>(PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType())),
                Mockito.argThat(new ObjectEqualsMathcer<>(BigDecimal.valueOf(-100))),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getRemarks())));

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(Mockito.any());

        try{
            PayVO payVO = payService.pay(payParaDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.PAY_ERROR);
        }
    }

    @Test
    public void function_test(){
        payService.pay(new PaymentDetailDO());
    }
}
