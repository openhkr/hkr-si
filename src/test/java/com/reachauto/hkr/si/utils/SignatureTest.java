package com.reachauto.hkr.si.utils;

import com.reachauto.hkr.si.pojo.parameter.PaymentBalanceParameter;
import com.reachauto.hkr.si.utils.wechat.Signature;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SignatureTest {

    @Test
    public void getSignObject() throws IllegalAccessException{
        PaymentBalanceParameter parameter = new PaymentBalanceParameter();
        parameter.setOrderId("123");
        System.out.println(Signature.getSign(parameter, "123"));
    }

    @Test
    public void getSignMapWithKey(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", "123");
        System.out.println(Signature.getSign(map, "123"));
    }

    @Test
    public void getSignMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", "123");
        System.out.println(Signature.getSign(map));
    }
}
