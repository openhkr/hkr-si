package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.service.impl.WeChatRefundServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


//@RunWith(PowerMockRunner.class)
public class RefundControllerTest {

    @InjectMocks
    private RefundController controller = new RefundController();

    @Mock
    private WeChatRefundServiceImpl weChatRefundServiceImpl;

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentSource(4);
        Mockito.doReturn(paymentDetailDO).when(paymentDetailRepository).findById(Mockito.anyLong());
        Mockito.doReturn(RefundBO.getSuccessInstants()).when(weChatRefundServiceImpl).refund(Mockito.any());
    }

    @Test
    public void refund_success(){

    }
}