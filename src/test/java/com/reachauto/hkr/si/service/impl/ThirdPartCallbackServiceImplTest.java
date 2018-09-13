package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.*;
import com.reachauto.hkr.si.mockclass.MockHttpServletRequest;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.PaymentResultLogRepository;
import com.reachauto.hkr.si.pojo.bo.CallbackBO;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.dto.CallbackCheckDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
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

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-28 11:16
 */

public class ThirdPartCallbackServiceImplTest {

    @InjectMocks
    private ThirdPartCallbackServiceImpl thirdPartCallbackService = new ThirdPartCallbackServiceImpl();

    @Mock
    private AliPayManager aliPayManager;

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Mock
    private PaymentResultLogRepository paymentResultLogRepository;

    @Mock
    private BizCallbackService bizCallbackService;

    @Mock
    private WeChatManager weChatManager;

    @Mock
    private BalanceManager balanceManager;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAliParams_map(){
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        Map<String, String> map = thirdPartCallbackService.getAliParams(httpServletRequest);
        Assert.assertEquals(map.get("111"), "123");
    }

    @Test
    public void getWeChatParams_map() throws IOException {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        InputStream inputStream = new ByteArrayInputStream("<xml><s111>123</s111></xml>".getBytes());
        httpServletRequest.setInputStream(inputStream);
        Map<String, Object> map = thirdPartCallbackService.getWeChatParams(httpServletRequest);
        Assert.assertEquals(map.get("s111"), "123");
    }

    @Test
    public void aliPayCheck_failed(){
        Map<String, String> paramMap = new HashMap<>();
        Mockito.doReturn(false).when(aliPayManager).check(paramMap);
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.aliPayCheck(paramMap);
        Assert.assertFalse(callbackCheckDTO.getCheckResult());
    }

    @Test
    public void aliPayCheck_success(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("trade_status", "TRADE_SUCCESS");
        paramMap.put("out_trade_no", "123456");
        paramMap.put("total_amount", "10");
        Mockito.doReturn(true).when(aliPayManager).check(paramMap);
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(10));
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(Long.valueOf(123456));
        Mockito.doNothing().when(paymentResultLogRepository).create(Mockito.any());
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.aliPayCheck(paramMap);
        Assert.assertTrue(callbackCheckDTO.getCheckResult());
    }

    @Test
    public void aliPayCheck_amountError(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("trade_status", "TRADE_SUCCESS");
        paramMap.put("out_trade_no", "123456");
        paramMap.put("total_amount", "100");
        Mockito.doReturn(true).when(aliPayManager).check(paramMap);
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(10));
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(Long.valueOf(123456));
        Mockito.doNothing().when(paymentResultLogRepository).create(Mockito.any());
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.aliPayCheck(paramMap);
        Assert.assertFalse(callbackCheckDTO.getCheckResult());
    }

    @Test
    public void aliPayCheck_paramError(){
        Map<String, String> paramMap = new HashMap<>();
        Mockito.doReturn(true).when(aliPayManager).check(paramMap);
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(10));
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(Long.valueOf(123456));
        Mockito.doNothing().when(paymentResultLogRepository).create(Mockito.any());
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.aliPayCheck(paramMap);
        Assert.assertFalse(callbackCheckDTO.getCheckResult());
    }

    @Test
    public void weChatCheck_failed(){
        Map<String, Object> paramMap = new HashMap<>();
        Mockito.doReturn(false).when(weChatManager).weChatCheck(paramMap);
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.weChatCheck(paramMap);
        Assert.assertFalse(callbackCheckDTO.getCheckResult());
    }

    @Test
    public void weChatCheck_success(){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("result_code", "SUCCESS");
        paramMap.put("out_trade_no", "123456");
        paramMap.put("total_amount", "1000");
        Mockito.doReturn(true).when(weChatManager).weChatCheck(paramMap);
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeTotalFee(new BigDecimal("10.00"));
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(Long.valueOf(123456));
        Mockito.doNothing().when(paymentResultLogRepository).create(Mockito.any());
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.weChatCheck(paramMap);
        Assert.assertTrue(callbackCheckDTO.getCheckResult());
    }

    @Test
    public void afterCallback_success(){

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_PROCESSING.getCode());
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setUserId("456");
        paymentDetailDO.setPaymentId("1234");
        paymentDetailDO.setTradeTotalFee(new BigDecimal("10"));

        CallbackBO callbackBO = new CallbackBO();
        callbackBO.setTradeStatus(true);

        CallbackCheckDTO callbackCheckDTO = new CallbackCheckDTO();
        callbackCheckDTO.setCheckResult(true);
        callbackCheckDTO.setPaymentDetailDO(paymentDetailDO);
        callbackCheckDTO.setCallbackBO(callbackBO);

        InnerResultBO balanceResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance("1234",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                paymentDetailDO.getTradeTotalFee(),
                paymentDetailDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(Mockito.any());
        Mockito.doNothing().when(bizCallbackService).bizCallBack(Mockito.any());

        thirdPartCallbackService.afterCallback(callbackCheckDTO);
    }

    @Test
    public void afterCallback_timeout(){

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setUserId("456");
        paymentDetailDO.setPaymentId("1234");
        paymentDetailDO.setTradeTotalFee(new BigDecimal("10"));

        CallbackBO callbackBO = new CallbackBO();
        callbackBO.setTradeStatus(true);

        CallbackCheckDTO callbackCheckDTO = new CallbackCheckDTO();
        callbackCheckDTO.setCheckResult(true);
        callbackCheckDTO.setPaymentDetailDO(paymentDetailDO);
        callbackCheckDTO.setCallbackBO(callbackBO);

        InnerResultBO balanceResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance("1234",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                paymentDetailDO.getTradeTotalFee(),
                paymentDetailDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(Mockito.any());
        Mockito.doNothing().when(bizCallbackService).bizCallBack(Mockito.any());
        try {
            thirdPartCallbackService.afterCallback(callbackCheckDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
        }
    }

    @Test
    public void afterCallback_failed(){

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setUserId("456");
        paymentDetailDO.setPaymentId("1234");
        paymentDetailDO.setTradeTotalFee(new BigDecimal("10"));

        CallbackBO callbackBO = new CallbackBO();
        callbackBO.setTradeStatus(true);

        CallbackCheckDTO callbackCheckDTO = new CallbackCheckDTO();
        callbackCheckDTO.setCheckResult(false);
        callbackCheckDTO.setPaymentDetailDO(paymentDetailDO);
        callbackCheckDTO.setCallbackBO(callbackBO);

        InnerResultBO balanceResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance("1234",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                paymentDetailDO.getTradeTotalFee(),
                paymentDetailDO.getRemarks());

        Mockito.doReturn(1).when(paymentDetailRepository).finishTrade(Mockito.any());
        Mockito.doNothing().when(bizCallbackService).bizCallBack(Mockito.any());

        thirdPartCallbackService.afterCallback(callbackCheckDTO);
    }
}
