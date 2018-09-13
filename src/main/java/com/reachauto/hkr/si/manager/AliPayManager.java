package com.reachauto.hkr.si.manager;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.reachauto.hkr.si.config.AliPayConfig;
import com.reachauto.hkr.si.config.NotifyUrlProperties;
import com.reachauto.hkr.si.persistence.PaymentResultLogRepository;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/1/15.
 */
@Slf4j
@Component
@EnableConfigurationProperties(NotifyUrlProperties.class)
public class AliPayManager extends BaseAliPayManager{

    @Resource(name="alipayClient")
    private AlipayClient alipayClient;

    @Autowired
    public void printNotifyUrlProperties(NotifyUrlProperties notifyUrl){
        super.notifyUrl = notifyUrl;
    }

    @Autowired
    public void printPaymentResultLogRepository(PaymentResultLogRepository paymentResultLogRepository){
        super.paymentResultLogRepository = paymentResultLogRepository;
    }

    @Override
    public AlipayClient getAlipayClient() {
        return alipayClient;
    }

    @Override
    public Integer getPaymentSource() {
        return PaymentSourceEnum.ALI.getCode();
    }

    @Override
    public boolean check(Map<String, String> params) {
        try {
            log.info("支付宝回调参数验签：{}", params);
            boolean result = AlipaySignature.rsaCheckV1(params, AliPayConfig.ALI_PUB_KEY, AliPayConfig.INPUT_CHARSET, AliPayConfig.SIGN_TYPE);
            result = result && Objects.equals(AliPayConfig.APP_ID, params.get("app_id"));
            log.info("支付宝验签结果：{}", result);
            return result;
        } catch (AlipayApiException e) {
            log.error("支付宝验签异常,{}", e);
            return false;
        }
    }
}
