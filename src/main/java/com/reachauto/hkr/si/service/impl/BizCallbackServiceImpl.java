package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.bo.BizCallbackBO;
import com.reachauto.hkr.si.pojo.enu.FinishBizCallbackEnum;
import com.reachauto.hkr.si.service.BizCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @author zhangshuo
 */
@Slf4j
@Service
public class BizCallbackServiceImpl implements BizCallbackService {

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @Override
    public void bizCallBack(PaymentDetailDO paymentDetailDO) {
        if (Objects.isNull(paymentDetailDO)) {
            return;
        }

        // 更新成功后发起回调
        BizCallbackBO bizCallbackBO = new BizCallbackBO();
        bizCallbackBO.setOrderId(paymentDetailDO.getOrderId());
        bizCallbackBO.setUserId(paymentDetailDO.getUserId());
        bizCallbackBO.setTradeTotalFee(paymentDetailDO.getTradeTotalFee().toString());
        bizCallbackBO.setTradeStatus(paymentDetailDO.getTradeStatus());
        bizCallbackBO.setPaymentId(paymentDetailDO.getPaymentId());
        bizCallbackBO.setPaymentSource(String.valueOf(paymentDetailDO.getPaymentSource()));
        bizCallbackBO.setThirdPayTransactionId(paymentDetailDO.getTradeNo());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Response result;
        try {
            String url = paymentDetailDO.getBizCallbackUrl();
            log.info("执行业务回调".concat(url));
            result = restTemplate.postForObject(url, new HttpEntity(bizCallbackBO, headers), Response.class);
            if (Objects.equals(0, result.getCode())) {
                /**
                 * 回调成功之后修改支付状态为已回调
                 */
                PaymentDetailDO paramDO1 = new PaymentDetailDO();
                paramDO1.setPaymentId(paymentDetailDO.getPaymentId());
                paramDO1.setFinishBizCallback(FinishBizCallbackEnum.HAS_CALL_BACK.getCode());
                paymentDetailRepository.update(paramDO1);
            }else{
                log.error("回调返回失败" + result.getCode() + " " + result.getDescription());
            }
        } catch (Exception e) {
            log.error("业务回调异常, ", e);
        }

    }
}
