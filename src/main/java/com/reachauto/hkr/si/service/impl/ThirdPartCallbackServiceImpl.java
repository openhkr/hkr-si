package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.entity.PaymentResultLogDO;
import com.reachauto.hkr.si.manager.*;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.PaymentResultLogRepository;
import com.reachauto.hkr.si.pojo.bo.CallbackBO;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.dto.CallbackCheckDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentResultLogTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.BizCallbackService;
import com.reachauto.hkr.si.service.ThirdPartCallbackService;
import com.reachauto.hkr.si.utils.GsonTool;
import com.reachauto.hkr.si.utils.ThreadPoolTool;
import com.reachauto.hkr.si.utils.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/1/19.
 */
@Slf4j
@Service
public class ThirdPartCallbackServiceImpl implements ThirdPartCallbackService {

    private static final int CACHE_SIZE = 1024;

    @Autowired
    private AliPayManager aliPayManager;

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @Autowired
    private PaymentResultLogRepository paymentResultLogRepository;

    @Autowired
    private BizCallbackService bizCallbackService;

    @Autowired
    private WeChatManager weChatManager;

    @Autowired
    private BalanceManager balanceManager;

    @Override
    public Map<String, String> getAliParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        requestParams.keySet().forEach(o -> {
            String name = (String) o;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        });
        return params;
    }

    @Override
    public Map<String, Object> getWeChatParams(HttpServletRequest request) {
        byte[] bytes = new byte[CACHE_SIZE * CACHE_SIZE];
        InputStream is;
        String reqDataXml = null;
        try {
            is = request.getInputStream();

            int nRead = 1;
            int nTotalRead = 0;
            while (nRead > 0) {
                nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
                if (nRead > 0) {
                    nTotalRead = nTotalRead + nRead;
                }
            }
            //获取返回的回调信息流
            reqDataXml = new String(bytes, 0, nTotalRead, "UTF-8");
            log.info("微信回调字符串{}", reqDataXml);
        } catch (IOException e) {
            log.error("读取request时出错:{}", e);
        }

        Map<String, Object> reqMap = new HashMap<>();
        try {
            Map<String, String> reqDataXmlMap = WXPayUtil.xmlToMap(reqDataXml);
            if (reqDataXmlMap != null) {
                reqMap.putAll(reqDataXmlMap);
            }
        } catch (Exception e) {
            log.error("转义request时出错:{}", e);
        }

        log.info("微信回调数据:{}", reqMap);
        return reqMap;
    }

    @Override
    public CallbackCheckDTO aliPayCheck(Map<String, String> params) {
        // 支付宝验签
        if(aliPayManager.check(params)){
            //业务验证  验证金额是否相等
            return bizCheck(CallbackBO.buildFromAliParams(params), GsonTool.objectToAllFieldNullJson(params));
        }
        log.info("支付宝验签失败");
        return CallbackCheckDTO.getCheckFailInstant();
    }

    @Override
    public CallbackCheckDTO weChatCheck(Map<String, Object> reqMap) {
        // 微信验签
        if(weChatManager.weChatCheck(reqMap)){
            //业务验证  验证金额是否相等
            return bizCheck(CallbackBO.buildFromWeChatParams(reqMap), GsonTool.objectToAllFieldNullJson(reqMap));
        }
        log.info("微信验签失败");
        return CallbackCheckDTO.getCheckFailInstant();
    }

    @Override
    public void afterCallback(CallbackCheckDTO callbackCheckDTO) {
        if (!callbackCheckDTO.getCheckResult()) {
            return;
        }
        PaymentDetailDO paymentDetailDO = callbackCheckDTO.getPaymentDetailDO();
        // 判断回调可处理状态
        if (paymentDetailDO.getTradeStatus().intValue() != TradeStatusEnum.TRADE_PROCESSING.getCode()
                && paymentDetailDO.getTradeStatus().intValue() != TradeStatusEnum.TRADE_SETTLEMENT.getCode()) {
            return;
        }
        CallbackBO callbackBO = callbackCheckDTO.getCallbackBO();
        paymentDetailDO.setTradeNo(callbackBO.getTradeNo());
        paymentDetailDO.setBuyerId(callbackBO.getBuyerId());
        paymentDetailDO.setTradeStatus(callbackBO.getTradeStatus() ? TradeStatusEnum.TRADE_SUCCESS.getCode() : TradeStatusEnum.TRADE_FAIL.getCode());

        // 如果是充值，处理加余额，失败抛异常中断操作
        if (callbackBO.getTradeStatus() && PaymentTypeEnum.RECHARGE.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
            // 根据OrderType判断是普通余额充值还是保证金余额充值
            InnerResultBO balanceResultBO = balanceManager.modifyBalance(String.valueOf(paymentDetailDO.getPaymentId()),
                    paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                    paymentDetailDO.getTradeTotalFee(), paymentDetailDO.getRemarks());
            // 超时或者失败都抛出异常，等待重试
            if (!balanceResultBO.isSuccessed()) {
                // 这个地方测试一下，抛异常时，第三方是否会处理失败
                throw new HkrBusinessException();
            }
        }

        // 更新payment_detail表
        if (paymentDetailRepository.finishTrade(paymentDetailDO) > 0) {
            // 更新成功后发起回调
            ThreadPoolTool.execute(() -> bizCallbackService.bizCallBack(paymentDetailDO));
        }
    }

    private CallbackCheckDTO bizCheck(CallbackBO callbackBO, String reqString) {
        if (callbackBO == null) {
            log.info("参数异常");
            return CallbackCheckDTO.getCheckFailInstant();
        }
        PaymentDetailDO paymentDetailDO = paymentDetailRepository.findById(Long.valueOf(callbackBO.getOutTradeNo()));
        if (paymentDetailDO == null || !Objects.equals(callbackBO.getTotalMount(), paymentDetailDO.getTradeTotalFee())) {
            log.info("金额无法对应，验签失败");
            return CallbackCheckDTO.getCheckFailInstant();
        }
        //记录结果日志
        ThreadPoolTool.execute(() -> {
            PaymentResultLogDO paymentResultLogDO = PaymentResultLogDO.buildFromAliPayCallbackBO(callbackBO);
            paymentResultLogDO.setLogType(PaymentResultLogTypeEnum.CALLBACK.getCode());
            paymentResultLogDO.setResult(reqString);
            paymentResultLogRepository.create(paymentResultLogDO);
        });
        return CallbackCheckDTO.getCheckSuccessInstant(callbackBO, paymentDetailDO);
    }
}
