package com.reachauto.hkr.si.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2018/1/15.
 */
@Configuration
public class AliPayConfig {

    public static final String GATEWAY_PATH = "https://openapi.alipay.com/gateway.do";

    // app id
    public static final String APP_ID = "";

    // 商户的私钥
    public static final String PRIVATE_KEY = "";

    // 支付宝的公钥，无需修改该值
    public static final String ALI_PUB_KEY = "";

    // 签名方式 不需修改
    public static final String SIGN_TYPE = "RSA2";

    // 格式 不需修改
    public static final String REQ_FORMAT = "json";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static final String INPUT_CHARSET = "utf-8";

    @Bean(name="alipayClient")
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
                GATEWAY_PATH,
                APP_ID,
                PRIVATE_KEY,
                REQ_FORMAT,
                INPUT_CHARSET,
                ALI_PUB_KEY,
                SIGN_TYPE
        );
    }
}
