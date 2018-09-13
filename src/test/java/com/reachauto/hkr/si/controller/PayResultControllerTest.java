package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-28 14:32
 */

public class PayResultControllerTest {

    @InjectMocks
    private PayResultController controller = new PayResultController();

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void settlement(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setTradeStatus(4);
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(1L);
        Mockito.doReturn(1).when(paymentDetailRepository).settlementTrade(Mockito.any());
        Response response = controller.settlement("1");
        Assert.assertEquals(0, response.getCode());
    }

    @Test
    public void settlement_null(){
        Mockito.doReturn(null).when(paymentDetailRepository).findById(1L);
        Mockito.doReturn(1).when(paymentDetailRepository).settlementTrade(Mockito.any());
        thrown.expect(HkrBusinessException.class);
        thrown.expectMessage("no.record");
        Response response = controller.settlement("1");
    }

    @Test
    public void settlement_paramError(){
        thrown.expect(HkrBusinessException.class);
        thrown.expectMessage("no.record");
        Response response = controller.settlement("q");
    }
}
