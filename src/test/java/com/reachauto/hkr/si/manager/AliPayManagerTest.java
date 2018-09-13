package com.reachauto.hkr.si.manager;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.reachauto.hkr.SiApplication;
import com.reachauto.hkr.si.config.AliPayConfig;
import com.reachauto.hkr.si.config.NotifyUrlProperties;
import com.reachauto.hkr.si.persistence.PaymentResultLogRepository;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2018/1/15.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = SiApplication.class)
public class AliPayManagerTest {

    @InjectMocks
    private AliPayManager aliPayManager = new AliPayManager();

    @Mock
    private AlipayClient alipayClient;

    @Mock
    private NotifyUrlProperties notifyUrl;

    @Mock
    private PaymentResultLogRepository paymentResultLogRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn("").when(notifyUrl).getAlipay();
    }

    @Test
    public void pay() throws Exception {
//        AlipayTradeAppPayRequest assRequest = new AlipayTradeAppPayRequest();
//
//        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
//        //商户网站唯一订单号
//        model.setOutTradeNo("test001");
//        //商品名称
//        model.setSubject("subject");
//        //商品详情
//        model.setBody("body");
//        //商品金额
//        model.setTotalAmount("price");
//
//        model.setTimeoutExpress(AliPayConfig.TIME_OUT_EXPRESS);
//        model.setProductCode(AliPayConfig.PRODUCT_CODE);
//        model.setGoodsType(AliPayConfig.GOOD_TYPE);
//
//        assRequest.setBizModel(model);
//        //回调地址
//        assRequest.setNotifyUrl("".concat("/").concat("token"));
//        AlipayTradeAppPayResponse assResult = new AlipayTradeAppPayResponse();
//        assResult.setBody("result");
//        Mockito.doReturn(assResult).when(alipayClient).sdkExecute(assRequest);
//        String result = aliPayManager.pay("subject", "body", "0.01", "test001", "token");
//        Assert.assertEquals("result", result);
    }

    @Test
    public void query() throws Exception {

    }

    @Test
    public void close() throws Exception {

    }

    @Test
    public void check() throws Exception {

    }

    @Autowired
    private AliPayManager aliPayManager1;

    //@Test
    public void pay1() throws Exception {
        String result = aliPayManager1.pay("subject", "body", "0.01", "zdtest001", "fafatoken");
        System.out.println(result);
    }

    //@Test
    public void query1() throws Exception {
        PaymentQueryBO result = aliPayManager1.query("1A2018010909094524444", null);
    }

    //@Test
    public void close1() throws Exception {
        boolean result = aliPayManager1.close("1A2018010909094524444", null);

    }

}