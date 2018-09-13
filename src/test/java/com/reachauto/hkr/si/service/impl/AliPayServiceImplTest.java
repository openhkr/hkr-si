package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.cache.PaymentIdGenerator;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.AliPayManager;
import com.reachauto.hkr.si.mathcer.ObjectEqualsMathcer;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
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
 * @create 2018-02-27 11:45
 */

public class AliPayServiceImplTest {

    @InjectMocks
    private AliPayServiceImpl payService = new AliPayServiceImpl();

    @Mock
    private AliPayManager aliPayManager;

    @Mock
    protected PaymentDetailRepository paymentDetailRepository;

    @Mock
    private PaymentIdGenerator paymentIdGenerator;

    private String rcTocken = "760809fc2c3a5dd06692e3c6ceea54f7";

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
        payParaDTO.setUserId("1234");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        OrderTypeEnum orderType = OrderTypeEnum.getType(paymentDetailDO.getOrderType());
        String sign = "1faf8894-1b82-11e8-b49d-000c293e4870";
        Mockito.doReturn(sign).when(aliPayManager).pay(
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getTradeTotalFee().toString())),
                Mockito.anyString(),
                Mockito.anyString());

        PayVO payVO = payService.pay(payParaDTO);
        Assert.assertEquals(sign, payVO.getSignedStr());
    }

    @Test
    public void pay_failed(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setUserId("12394");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        OrderTypeEnum orderType = OrderTypeEnum.getType(paymentDetailDO.getOrderType());
        String sign = null;
        Mockito.doReturn(sign).when(aliPayManager).pay(
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getTradeTotalFee().toString())),
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(rcTocken.toString())));

        try{
            PayVO payVO = payService.pay(payParaDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.PAY_ERROR);
        }
    }

    @Test
    public void hasSuccessHistory_pay_failed(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setUserId("12394");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        OrderTypeEnum orderType = OrderTypeEnum.getType(paymentDetailDO.getOrderType());
        String sign = null;
        Mockito.doReturn(sign).when(aliPayManager).pay(
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getTradeTotalFee().toString())),
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(rcTocken.toString())));

        try{
            PayVO payVO = payService.pay(payParaDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_FINISHED);
        }
    }

    @Test
    public void hasSettlementHistory_pay_failed(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setUserId("12394");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SETTLEMENT.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        OrderTypeEnum orderType = OrderTypeEnum.getType(paymentDetailDO.getOrderType());
        String sign = null;
        Mockito.doReturn(sign).when(aliPayManager).pay(
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getTradeTotalFee().toString())),
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(rcTocken.toString())));

        try{
            PayVO payVO = payService.pay(payParaDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_SETTLEMENT);
        }
    }

    @Test
    public void hasProcessingSameHistory_pay_success(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        payParaDTO.setUserId("12394");
        payParaDTO.setPrice(BigDecimal.valueOf(100));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        OrderTypeEnum orderType = OrderTypeEnum.getType(paymentDetailDO.getOrderType());
        String sign = "54165481654sd8f48e48f4e";
        Mockito.doReturn(sign).when(aliPayManager).pay(
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getTradeTotalFee().toString())),
                Mockito.anyString(),
                Mockito.anyString());

        PayVO payVO = payService.pay(payParaDTO);
        Assert.assertEquals(sign, payVO.getSignedStr());
    }

    @Test
    public void hasProcessingSameHistoryNewAmount_pay_success(){
        PayParaDTO payParaDTO = new PayParaDTO();
        payParaDTO.setOrderId("1");
        payParaDTO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        payParaDTO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        payParaDTO.setUserId("12394");
        payParaDTO.setPrice(BigDecimal.valueOf(10));

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("1234");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(100));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        List<PaymentDetailDO> paymentDetailDOList = new ArrayList<>();
        paymentDetailDOList.add(paymentDetailDO);

        Mockito.doReturn(paymentDetailDOList).when(paymentDetailRepository).findByOrderTypeAndOrderId(payParaDTO.getOrderType(), payParaDTO.getOrderId());
        Mockito.doNothing().when(paymentDetailRepository).create(Mockito.any());

        OrderTypeEnum orderType = OrderTypeEnum.getType(paymentDetailDO.getOrderType());
        String sign = "54165481654sd8f48e48f4e";
        Mockito.doReturn(sign).when(aliPayManager).pay(
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.argThat(new ObjectEqualsMathcer<>("10")),
                Mockito.anyString(),
                Mockito.anyString());

        PayVO payVO = payService.pay(payParaDTO);
        Assert.assertEquals(sign, payVO.getSignedStr());
    }
}
