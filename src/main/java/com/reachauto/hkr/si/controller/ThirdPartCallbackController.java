package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.si.pojo.dto.CallbackCheckDTO;
import com.reachauto.hkr.si.service.ThirdPartCallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/16.
 */
@Slf4j
@Api(value = "API - ThirdPartCallbackController", tags = "third_part_callback")
@RestController
@RequestMapping(value = "/api/v2/third/callback")
public class ThirdPartCallbackController {

    private static final String ALI_SUCCESS = "success";
    private static final String ALI_FAIL = "failure";


    private static final String WECHAT_SUCCESS = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    private static final String WECHAT_FAIL = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[FAIL]]></return_msg></xml>";


    @Autowired
    private ThirdPartCallbackService thirdPartCallbackService;


    @ApiOperation(value = "支付宝三方回调接口", notes = "支付宝三方回调接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "environment", required = true, dataType = "String", paramType = "path")
    })
    @RequestMapping(value = "ali_pay/{environment}", method = RequestMethod.POST)
    public void aliPayCallback(HttpServletRequest request, HttpServletResponse resp) {
        //获取参数信息
        Map<String, String> params = thirdPartCallbackService.getAliParams(request);
        //验证
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.aliPayCheck(params);
        String result;
        // 此处处理失败会抛异常
        thirdPartCallbackService.afterCallback(callbackCheckDTO);
        if (callbackCheckDTO.getCheckResult()) {
            result = ALI_SUCCESS;
        } else {
            result = ALI_FAIL;
        }
        sendResponse(resp, result);
    }

    @ApiOperation(value = "微信三方回调接口", notes = "微信三方回调接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "environment", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "xml", value = "微信支付结果的回调内容,格式为XML", required = true, dataType = "string", paramType = "body")
    })
    @RequestMapping(value = "we_chat_pay/{environment}", method = RequestMethod.POST)
    public void weChatPayCallback(HttpServletRequest request, HttpServletResponse response) {
        //获取参数信息
        Map<String, Object> params = thirdPartCallbackService.getWeChatParams(request);
        //验证
        CallbackCheckDTO callbackCheckDTO = thirdPartCallbackService.weChatCheck(params);
        String result;
        // 此处处理失败会抛异常
        thirdPartCallbackService.afterCallback(callbackCheckDTO);
        if (callbackCheckDTO.getCheckResult()) {
            result = WECHAT_SUCCESS;
        } else {
            result = WECHAT_FAIL;
        }
        sendResponse(response, result);
    }

    private static void sendResponse(HttpServletResponse resp, String result) {
        log.info("返回三方数据：" + result);
        try {
            OutputStream outputStream = resp.getOutputStream();
            byte[] dataByteArr = result.getBytes("UTF-8");
            outputStream.write(dataByteArr);
        } catch (IOException e) {
            log.error("发送三方回调结果失败", e);
        }
    }

}
