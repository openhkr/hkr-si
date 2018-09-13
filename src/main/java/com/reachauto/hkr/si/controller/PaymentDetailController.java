package com.reachauto.hkr.si.controller;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.common.response.ResponseHelper;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/9/14.
 */

@Api(value = "支付流水信息查询")
@RestController
@RequestMapping(value = "/api/v2/payment")
public class PaymentDetailController {

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    @ApiOperation(value = "获取支付流水", notes = "功能：查询指定用户的用户余额流水列表，使用场景：查询C端登录用户的余额流水列表，错误代码：无")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_id", value = "用户ID", dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "订单类型, 1是租车订单, 2是充电订单, 3,新的充电, 4 用户缴费, 8 租车押金, 16 充值", dataType = "int", paramType = "query")
    })
    @RequestMapping(value = "user_id={user_id}/sum", method = RequestMethod.GET)
    public Response<BigDecimal> sum( @PathVariable("user_id") String userId, @RequestParam(name = "type") int type) {
        return ResponseHelper.createSuccessResponse(paymentDetailRepository.sumfeeByUserAndOrderType(userId, type));
    }
}
