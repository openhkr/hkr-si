package com.reachauto.hkr.si.pojo.dto;

import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.pojo.bo.CallbackBO;
import lombok.Data;

/**
 * Created by Administrator on 2018/1/19.
 */
@Data
public class CallbackCheckDTO {
    private Boolean checkResult;
    private CallbackBO callbackBO;
    private PaymentDetailDO paymentDetailDO;

    public static CallbackCheckDTO getCheckFailInstant() {
        CallbackCheckDTO callbackCheckDTO = new CallbackCheckDTO();
        callbackCheckDTO.setCheckResult(false);
        return callbackCheckDTO;
    }

    public static CallbackCheckDTO getCheckSuccessInstant(CallbackBO callbackBO, PaymentDetailDO paymentDetailDO) {
        CallbackCheckDTO callbackCheckDTO = new CallbackCheckDTO();
        callbackCheckDTO.setCheckResult(true);
        callbackCheckDTO.setCallbackBO(callbackBO);
        callbackCheckDTO.setPaymentDetailDO(paymentDetailDO);
        return callbackCheckDTO;
    }

}
