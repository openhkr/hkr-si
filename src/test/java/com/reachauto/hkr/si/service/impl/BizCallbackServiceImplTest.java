package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.common.response.ResponseHelper;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.mathcer.ObjectEqualsMathcer;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-28 10:51
 */

public class BizCallbackServiceImplTest {

    @InjectMocks
    private BizCallbackServiceImpl callbackService = new BizCallbackServiceImpl();

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void bizCallBack_null(){
        callbackService.bizCallBack(null);
    }

    @Test
    public void bizCallBack_success(){

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        paymentDetailDO.setBizCallbackUrl("http://1234");

        Response result = ResponseHelper.createSuccessResponse();
        // Template mock失败
        Mockito.doReturn(result).when(restTemplate).postForObject(
                Mockito.any(),
                Mockito.any(),
                Mockito.any());

        Mockito.doReturn(1).when(paymentDetailRepository).update(Mockito.any());

        callbackService.bizCallBack(paymentDetailDO);
    }
}
