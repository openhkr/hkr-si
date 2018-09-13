package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.common.response.ResponseHelper;
import com.reachauto.hkr.si.utils.ApplicationContextTool;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.bo.BizCallbackBO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.PayQueryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Created by Administrator on 2018/1/16.
 * @author zhangshuo
 */
@Api(value = "API - PayResultController", tags = "pay_result")
@RestController
@RequestMapping(value = "/api/v2/result")
public class PayResultController {

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @ApiOperation(value = "支付确认", notes = "手机端完成支付时调用")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 请求已完成"),
            @ApiResponse(code = 14003, message = "无当前流水记录")}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paymentId", required = true, dataType = "String", paramType = "path")
    })
    @RequestMapping(value = "payment_id={paymentId}/settlement", method = RequestMethod.POST, headers = "api-version=1")
    public Response settlement(@PathVariable String paymentId) {
        /**
         * 查询paymentDetail表状态
         * 如果是处理中状态，变成结算中
         */
        Long paymentIdLong = null;
        try {
            paymentIdLong = Long.parseLong(paymentId);
        } catch (NumberFormatException e) {
            throw new HkrBusinessException(ErrCodeConstant.NO_RECORD, "no.record");
        }
        PaymentDetailDO paymentDetailDO = paymentDetailRepository.findById(paymentIdLong);
        if(Objects.isNull(paymentDetailDO)){
            throw new HkrBusinessException(ErrCodeConstant.NO_RECORD, "no.record");
        }
        if(paymentDetailDO.isProcessing()){
            // 处理中改成结算中
            paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SETTLEMENT.getCode());
            paymentDetailRepository.settlementTrade(paymentDetailDO);
        }
        return ResponseHelper.createSuccessResponse();
    }

    @ApiOperation(value = "支付确认", notes = "管理平台调用支付确认接口")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 请求已完成"),
            @ApiResponse(code = 14003, message = "无当前流水记录")}
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paymentId", required = true, dataType = "String", paramType = "path")
    })
    @RequestMapping(value = "payment_id={paymentId}/confirm", method = RequestMethod.POST, headers = "api-version=1")
    public Response<BizCallbackBO> confirm(@PathVariable String paymentId) {

        /**
         * 查询paymentDetail表状态
         * 如果是未完成状态，查询第三方支付
         * 查询成功根据需要回调业务系统
         */
        Long paymentIdLong;
        try {
            paymentIdLong = Long.parseLong(paymentId);
        } catch (NumberFormatException e) {
            throw new HkrBusinessException(ErrCodeConstant.NO_RECORD, "no.record");
        }
        PaymentDetailDO paymentDetailDO = paymentDetailRepository.findById(paymentIdLong);
        if(Objects.isNull(paymentDetailDO)){
            throw new HkrBusinessException(ErrCodeConstant.NO_RECORD, "no.record");
        }
        PaymentSourceEnum paymentSource = PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource());
        PayQueryService payQueryService = (PayQueryService) ApplicationContextTool.get(paymentSource.getQueryClazz());
        PaymentDetailDO result = payQueryService.orderQueryFull(paymentDetailDO);
        BizCallbackBO bizCallbackBO = new BizCallbackBO();
        bizCallbackBO.setOrderId(result.getOrderId());
        bizCallbackBO.setUserId(result.getUserId());
        bizCallbackBO.setTradeTotalFee(result.getTradeTotalFee().toString());
        bizCallbackBO.setTradeStatus(result.getTradeStatus());
        bizCallbackBO.setPaymentId(result.getPaymentId());
        return ResponseHelper.createSuccessResponse(bizCallbackBO);
    }
}
