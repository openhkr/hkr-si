package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.si.pojo.parameter.PaymentBalanceParameter;
import com.reachauto.hkr.si.pojo.parameter.PaymentRechargeParameter;
import com.reachauto.hkr.si.pojo.parameter.PaymentThirdParameter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

public class PayControllerTest {

    @InjectMocks
    private PayController controller = new PayController();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void pay_success(){
        PaymentThirdParameter paymentThirdParameter = new PaymentThirdParameter();
        paymentThirdParameter.setOrderId("9999559594999");
        paymentThirdParameter.setOrderType("1");
        paymentThirdParameter.setPaymentSource("2");
        paymentThirdParameter.setCallbackUrl("http://hkr-dy/api/v2/deposit/payment/2018052416030821000/callback");
        paymentThirdParameter.setUserId("999");
        paymentThirdParameter.setPrice(new BigDecimal("1.00"));
        thrown.expect(NullPointerException.class);
        controller.pay(paymentThirdParameter);
    }

    @Test
    public void balance_pay_success(){
        PaymentBalanceParameter paymentBalanceParameter = new PaymentBalanceParameter();
        paymentBalanceParameter.setOrderId("9999559594999");
        paymentBalanceParameter.setOrderType("1");
        paymentBalanceParameter.setPaymentSource("2");
        paymentBalanceParameter.setUserId("999");
        paymentBalanceParameter.setPrice(new BigDecimal("1.00"));
        thrown.expect(NullPointerException.class);
        controller.balancePay(paymentBalanceParameter);
    }

    @Test
    public void recharge_pay_success(){
        PaymentRechargeParameter paymentRechargeParameter = new PaymentRechargeParameter();
        paymentRechargeParameter.setOrderId("9999559594999");
        paymentRechargeParameter.setOrderType("16");
        paymentRechargeParameter.setPaymentSource("2");
        paymentRechargeParameter.setCallbackUrl("http://hkr-dy/api/v2/deposit/payment/2018052416030821000/callback");
        paymentRechargeParameter.setUserId("999");
        paymentRechargeParameter.setPrice(new BigDecimal("1.00"));
        thrown.expect(NullPointerException.class);
        controller.rechargePay(paymentRechargeParameter);
    }
}