package com.reachauto.hkr.si.pojo.bo;

import com.google.gson.annotations.SerializedName;
import com.reachauto.hkr.si.constant.AliPayConstants;
import com.reachauto.hkr.si.constant.WeChatConstant;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.utils.GsonTool;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/19.
 */
@Data
public class CallbackBO {

    private static final BigDecimal FEN_TO_YUAN_DIVISOR = BigDecimal.valueOf(100);
    private static final int SCALE_TWO = 2;

    @SerializedName(value = "totalMount", alternate = {"total_amount", "total_fee"})
    private BigDecimal totalMount;
    @SerializedName(value = "buyerId", alternate = {"buyer_id", "openid"})
    private String buyerId;
    @SerializedName(value = "tradeNo", alternate = {"trade_no", "transaction_id"})
    private String tradeNo;
    @SerializedName(value = "outTradeNo", alternate = {"out_trade_no"})
    private String outTradeNo;

    private Boolean tradeStatus;

    private Integer paymentSource;

    public static CallbackBO buildFromAliParams(Map<String, String> params) {
        if (ObjectUtils.isEmpty(params)) {
            return null;
        }
        String jsonStr = GsonTool.objectToAllFieldNullJson(params);
        CallbackBO callbackBO  = GsonTool.jsonToBean(jsonStr, CallbackBO.class);
        String tradeStatus = params.get("trade_status");
        if (AliPayConstants.TRADE_SUCCESS.equals(tradeStatus) || AliPayConstants.TRADE_FINISHED.equals(tradeStatus)) {
            callbackBO.setTradeStatus(true);
        } else {
            callbackBO.setTradeStatus(false);
        }
        callbackBO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        return callbackBO;
    }

    public static CallbackBO buildFromWeChatParams(Map<String, Object> reqMap) {
        if (ObjectUtils.isEmpty(reqMap)) {
            return null;
        }
        String jsonStr = GsonTool.objectToAllFieldNullJson(reqMap);
        CallbackBO callbackBO  = GsonTool.jsonToBean(jsonStr, CallbackBO.class);
        callbackBO.setTotalMount(callbackBO.getTotalMount().divide(FEN_TO_YUAN_DIVISOR, SCALE_TWO, BigDecimal.ROUND_HALF_UP));
        String tradeStatus = reqMap.get("result_code").toString();
        if (WeChatConstant.WECHAT_SUCCESS.equals(tradeStatus)) {
            callbackBO.setTradeStatus(true);
        } else {
            callbackBO.setTradeStatus(false);
        }
        callbackBO.setPaymentSource(PaymentSourceEnum.WECHAT.getCode());
        return callbackBO;
    }
}
