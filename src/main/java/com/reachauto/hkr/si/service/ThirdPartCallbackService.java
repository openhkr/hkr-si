package com.reachauto.hkr.si.service;

import com.reachauto.hkr.si.pojo.dto.CallbackCheckDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/18.
 */
public interface ThirdPartCallbackService {

    Map<String, String> getAliParams(HttpServletRequest request);

    Map<String, Object> getWeChatParams(HttpServletRequest request);

    CallbackCheckDTO aliPayCheck(Map<String, String> params);

    CallbackCheckDTO weChatCheck(Map<String, Object> reqMap);

    void afterCallback(CallbackCheckDTO callbackCheckDTO);
}
