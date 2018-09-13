package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
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
 * @create 2018-02-28 14:32
 */

public class PaymentDetailControllerTest {

    @InjectMocks
    private PaymentDetailController controller = new PaymentDetailController();

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sum(){
        Mockito.doReturn(new BigDecimal("1.00")).when(paymentDetailRepository).sumfeeByUserAndOrderType("123", 1);
        Response response = controller.sum("123", 1);
        Assert.assertEquals(new BigDecimal("1.00"), response.getPayload());
    }
}
