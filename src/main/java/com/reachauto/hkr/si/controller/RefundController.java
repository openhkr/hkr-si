package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.common.response.ResponseHelper;
import com.reachauto.hkr.si.utils.ApplicationContextTool;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.parameter.RefundParameter;
import com.reachauto.hkr.si.service.RefundManualService;
import com.reachauto.hkr.si.service.RefundService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "API - RefundControlledr", tags = "refund")
@Slf4j
@RestController
@RequestMapping(value = "/api/v2/refund")
public class RefundController {

    /**
     * 微信支付订单超时时间
     */
    private static final long WEIXIN_TX_TIMEOUT = 364L * 24L * 60L * 60L * 1000L;

    /**
     * 阿里支付订单超时时间
     */
    private static final long ALI_TX_TIMEOUT = 89L * 24L * 60L * 60L * 1000L;

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @Autowired
    private RefundManualService refundManualService;

    @ApiOperation(value = "支付订单退款接口", notes = "支付订单退款接口")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 退款成功"),
            @ApiResponse(code = 14041, message = "订单不存在"),
            @ApiResponse(code = 14042, message = "订单已退款"),
            @ApiResponse(code = 14044, message = "退款金额错误"),
            @ApiResponse(code = 14045, message = "未支付成功的订单不可退款"),
            @ApiResponse(code = 14046, message = "退款进行中"),
            @ApiResponse(code = 14047, message = "退款失败"),
            @ApiResponse(code = 14048, message = "订单类型不正确"),
            @ApiResponse(code = 14049, message = "订单已经超过了三方允许退款的时间")
    })
    @RequestMapping(value = "/payment", method = RequestMethod.POST, headers = "api-version=1")
    public Response refundPayment(@RequestBody @Valid RefundParameter refundParameter) {

        // 拼装参数
        RefundDTO refundDTO = prepareRefundDTO(refundParameter);
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        // 判断订单类型
        if (!PaymentTypeEnum.PAYMENT.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_TYPE_ERROR, "order.type.error");
        }

        PaymentSourceEnum paymentSource = PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource());
        RefundService refundService = (RefundService) ApplicationContextTool.get(paymentSource.getRefundClazz());
        RefundBO refundBO = refundService.refund(refundDTO);
        // 如果是超时返回错误码
        if (refundBO.isNoResponse()) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUNDING, "order.refunding");
        }
        return ResponseHelper.createSuccessResponse();
    }

    @ApiOperation(value = "充值订单退款接口", notes = "充值订单退款接口")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 退款成功"),
            @ApiResponse(code = 14041, message = "订单不存在"),
            @ApiResponse(code = 14042, message = "订单已退款"),
            @ApiResponse(code = 14044, message = "退款金额错误"),
            @ApiResponse(code = 14045, message = "未支付成功的订单不可退款"),
            @ApiResponse(code = 14046, message = "退款进行中"),
            @ApiResponse(code = 14047, message = "退款失败"),
            @ApiResponse(code = 14048, message = "订单类型不正确"),
            @ApiResponse(code = 14007, message = "用户余额不足")
    })
    @RequestMapping(value = "/recharge", method = RequestMethod.POST, headers = "api-version=1")
    public Response refundRecharge(@RequestBody @Valid RefundParameter refundParameter) {

        // 拼装参数
        RefundDTO refundDTO = prepareRefundDTO(refundParameter);
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();

        // 判断订单类型
        if (!PaymentTypeEnum.RECHARGE.equals(PaymentTypeEnum.getType(paymentDetailDO.getPaymentType()))) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_TYPE_ERROR, "order.type.error");
        }

        PaymentSourceEnum paymentSource = PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource());
        RefundService refundService = (RefundService) ApplicationContextTool.get(paymentSource.getRefundClazz());
        RefundBO refundBO = refundService.refund(refundDTO);
        // 如果是超时返回错误码
        if (refundBO.isNoResponse()) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUNDING, "order.refunding");
        }
        return ResponseHelper.createSuccessResponse();
    }

    @ApiOperation(value = "财务人工退款接口", notes = "财务人工退款接口")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 退款成功"),
            @ApiResponse(code = 14041, message = "订单不存在"),
            @ApiResponse(code = 14042, message = "订单已退款"),
            @ApiResponse(code = 14044, message = "退款金额错误"),
            @ApiResponse(code = 14045, message = "未支付成功的订单不可退款"),
            @ApiResponse(code = 14046, message = "退款进行中"),
            @ApiResponse(code = 14047, message = "退款失败"),
            @ApiResponse(code = 14007, message = "用户余额不足")
    })
    @RequestMapping(value = "/manual", method = RequestMethod.POST, headers = "api-version=1")
    public Response refundManual(@RequestBody @Valid RefundParameter refundParameter) {

        // 拼装参数
        RefundDTO refundDTO = prepareRefundManualDTO(refundParameter);

        // 调用财务人工退款，出错直接在service抛异常
        RefundBO refundBO = refundManualService.refundManual(refundDTO);
        // 如果是超时返回错误码
        if (refundBO.isNoResponse()) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUNDING, "order.refunding");
        }
        return ResponseHelper.createSuccessResponse();
    }

    /**
     * 将参数转化成DTO
     *
     * @param refundParameter
     * @return
     */
    private RefundDTO prepareRefundDTO(RefundParameter refundParameter) {

        Long paymentId = null;
        try {
            paymentId = Long.parseLong(refundParameter.getPaymentId());
        } catch (Exception ex) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_NOTEX, "order.notex");
        }

        PaymentDetailDO paymentDetailDO = paymentDetailRepository.findById(paymentId);

        if (paymentDetailDO == null) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_NOTEX, "order.notex");
        }

        // 验证退款时间，支付宝超过90天，微信超过365天不能退款
        if(PaymentSourceEnum.WECHAT.getCode() == paymentDetailDO.getPaymentSource().intValue()){
            if(System.currentTimeMillis() - paymentDetailDO.getCreatedAt().getTime() > WEIXIN_TX_TIMEOUT){
                throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUND_TIMEOUT, "order.refund.timeout");
            }
        }else if(PaymentSourceEnum.ALI.getCode() == paymentDetailDO.getPaymentSource().intValue()){
            if(System.currentTimeMillis() - paymentDetailDO.getCreatedAt().getTime() > ALI_TX_TIMEOUT){
                throw new HkrBusinessException(ErrCodeConstant.ORDER_REFUND_TIMEOUT, "order.refund.timeout");
            }
        }

        return new RefundDTO(paymentDetailDO, refundParameter.getAmount(), refundParameter.getDesc());
    }


    /**
     * 将参数转化成DTO
     *
     * @param refundParameter
     * @return
     */
    private RefundDTO prepareRefundManualDTO(RefundParameter refundParameter) {

        Long paymentId = null;
        try {
            paymentId = Long.parseLong(refundParameter.getPaymentId());
        } catch (Exception ex) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_NOTEX, "order.notex");
        }

        PaymentDetailDO paymentDetailDO = paymentDetailRepository.findById(paymentId);

        if (paymentDetailDO == null) {
            throw new HkrBusinessException(ErrCodeConstant.ORDER_NOTEX, "order.notex");
        }

        return new RefundDTO(paymentDetailDO, refundParameter.getAmount(), refundParameter.getDesc());
    }
}
