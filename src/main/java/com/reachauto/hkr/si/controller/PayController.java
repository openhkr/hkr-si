package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.common.response.ResponseHelper;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.pojo.dto.PayParaDTO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.parameter.PaymentBalanceParameter;
import com.reachauto.hkr.si.pojo.parameter.PaymentRechargeParameter;
import com.reachauto.hkr.si.pojo.parameter.PaymentThirdParameter;
import com.reachauto.hkr.si.pojo.vo.BalancePayVO;
import com.reachauto.hkr.si.pojo.vo.PayVO;
import com.reachauto.hkr.si.service.PayService;
import com.reachauto.hkr.si.utils.ApplicationContextTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by Administrator on 2018/1/16.
 */
@Api(value = "API - PayController", tags = "payment")
@RestController
@RequestMapping(value = "/api/v2/payment")
public class PayController {

    @ApiOperation(value = "三方支付下单接口", notes = "三方支付下单接口，包括支付宝、微信、微信公众号")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 请求已完成", response = PayVO.class),
            @ApiResponse(code = 14001, message = "订单结算中"),
            @ApiResponse(code = 14002, message = "支付已完成"),
            @ApiResponse(code = 14006, message = "支付下单失败")}
    )
    @RequestMapping(value = "third", method = RequestMethod.POST, headers = "api-version=1")
    public Response<PayVO> pay(@RequestBody @Valid PaymentThirdParameter paymentThirdParameter) {
        PaymentSourceEnum paymentSource = PaymentSourceEnum.getType(Integer.valueOf(paymentThirdParameter.getPaymentSource()));
        if (paymentSource == null || paymentSource.getPayClazz() == null) {
            throw new HkrBusinessException(ErrCodeConstant.NO_SUCH_PAYMENT_TYPE, "no.such.payment.type");
        }

        String userId = paymentThirdParameter.getUserId();

        PayParaDTO payParaDTO = PayParaDTO.buildFromPayParameter(paymentThirdParameter);
        payParaDTO.setUserId(userId);

        PayService payService = (PayService) ApplicationContextTool.get(paymentSource.getPayClazz());

        PayVO payResult = payService.pay(payParaDTO);
        return ResponseHelper.createSuccessResponse(payResult);
    }

    @ApiOperation(value = "余额支付接口", notes = "余额支付接口，包括普通余额，保证金余额")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 请求已完成", response = BalancePayVO.class),
            @ApiResponse(code = 14001, message = "订单结算中"),
            @ApiResponse(code = 14002, message = "支付已完成"),
            @ApiResponse(code = 14006, message = "支付失败"),
            @ApiResponse(code = ErrCodeConstant.BALANCE_NOT_ENOUGH, message = "余额不足")}
    )
    @RequestMapping(value = "balance", method = RequestMethod.POST, headers = "api-version=1")
    public Response<BalancePayVO> balancePay(@RequestBody @Valid PaymentBalanceParameter paymentBalanceParameter) {

        PaymentSourceEnum paymentSource = PaymentSourceEnum.getType(Integer.valueOf(paymentBalanceParameter.getPaymentSource()));
        if (paymentSource == null || paymentSource.getPayClazz() == null) {
            throw new HkrBusinessException(ErrCodeConstant.NO_SUCH_PAYMENT_TYPE, "no.such.payment.type");
        }
        String userId = paymentBalanceParameter.getUserId();

        PayParaDTO payParaDTO = PayParaDTO.buildFromBalancePayParameter(paymentBalanceParameter);
        payParaDTO.setUserId(userId);

        PayService payService = (PayService) ApplicationContextTool.get(paymentSource.getPayClazz());

        PayVO payResult = payService.pay(payParaDTO);
        BalancePayVO balancePayVO = new BalancePayVO();
        balancePayVO.setPaymentId(payResult.getPaymentId());
        balancePayVO.setAmount(payParaDTO.getPrice());
        return ResponseHelper.createSuccessResponse(balancePayVO);
    }

    @ApiOperation(value = "充值接口", notes = "充值接口，包括普通余额充值和保证金余额充值，充值途径有支付宝，微信，微信公众号")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "Successful — 请求已完成", response = PayVO.class),
            @ApiResponse(code = 14001, message = "订单结算中"),
            @ApiResponse(code = 14002, message = "充值已完成"),
            @ApiResponse(code = 14006, message = "充值支付下单失败")}
    )
    @RequestMapping(value = "recharge", method = RequestMethod.POST, headers = "api-version=1")
    public Response<PayVO> rechargePay(@RequestBody @Valid PaymentRechargeParameter paymentRechargeParameter) {
        // 充值和支付基本相同，传的类型不一样，处理支付逻辑完成后会增加用户的余额
        PaymentSourceEnum paymentSource = PaymentSourceEnum.getType(Integer.valueOf(paymentRechargeParameter.getPaymentSource()));
        if (paymentSource == null || paymentSource.getPayClazz() == null) {
            throw new HkrBusinessException(ErrCodeConstant.NO_SUCH_PAYMENT_TYPE, "no.such.payment.type");
        }
        String userId = paymentRechargeParameter.getUserId();

        PayParaDTO payParaDTO = PayParaDTO.buildFromRechargePayParameter(paymentRechargeParameter);
        payParaDTO.setUserId(userId);

        PayService payService = (PayService) ApplicationContextTool.get(paymentSource.getPayClazz());

        PayVO payResult = payService.pay(payParaDTO);
        return ResponseHelper.createSuccessResponse(payResult);
    }
}
