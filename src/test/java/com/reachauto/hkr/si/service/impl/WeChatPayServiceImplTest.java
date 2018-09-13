package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.cache.PaymentIdGenerator;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.WeChatManager;
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

public class WeChatPayServiceImplTest {

    @InjectMocks
    private WeChatPayServiceImpl payService = new WeChatPayServiceImpl();

    @Mock
    private WeChatManager weChatManager;

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
        Mockito.doReturn(sign).when(weChatManager).weChartUnifiedOrder(
                Mockito.argThat(new ObjectEqualsMathcer<>(PaymentSourceEnum.WECHAT)),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getTradeTotalFee())),
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(null)));

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
        Mockito.doReturn(sign).when(weChatManager).weChartUnifiedOrder(
                Mockito.argThat(new ObjectEqualsMathcer<>(PaymentSourceEnum.WECHAT)),
                Mockito.argThat(new ObjectEqualsMathcer<>(orderType.getName())),
                Mockito.anyString(),
                Mockito.argThat(new ObjectEqualsMathcer<>(paymentDetailDO.getTradeTotalFee())),
                Mockito.argThat(new ObjectEqualsMathcer<>(rcTocken.toString())),
                Mockito.argThat(new ObjectEqualsMathcer<>(null)));

        try{
            PayVO payVO = payService.pay(payParaDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.PAY_ERROR);
        }
    }
}
