package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.constant.BalanceRemarkConstants;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.AliPayManager;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.manager.WeChatManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.RefundRecordRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
import com.reachauto.hkr.si.service.BizCallbackService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

/**
 * @author zhang.shuo-neu
 * @create 2018-02-26 9:04
 * 单元测试说明，共测试以下场景：
 * 支付宝支付退款
 *  --返回退款成功
 *  --退款进行中异常
 *  --超时异常
 * 支付宝值退款
 *  --返回退款成功
 *  --退款进行中异常
 *  --余额系统超时异常
 */
public class AliRefundServiceImplTest {

    @InjectMocks
    private AliRefundServiceImpl refundService = new AliRefundServiceImpl();

    @Mock
    protected PaymentDetailRepository paymentDetailRepository;

    @Mock
    protected BizCallbackService bizCallbackService;

    @Mock
    protected BalanceManager balanceManager;

    @Mock
    private AliPayManager aliPayManager;

    @Mock
    protected RefundRecordRepository refundRecordRepository;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void pay_refund_refundsuccess(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());
        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(aliPayManager).refund(paymentDetailDO.getPaymentId(), paymentDetailDO.getTradeNo(),
                "null", refundDTO.getAmount().toString(), refundDTO.getDesc());
        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        RefundBO refundBO = refundService.refund(refundDTO);
        Assert.assertTrue(refundBO.isSuccessed());
    }

    @Test
    public void pay_refund_refundingerror(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");
        Mockito.doReturn(0).when(paymentDetailRepository).refunding(paymentDetailDO);
        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_REFUNDING);
        }
    }

    @Test
    public void pay_refund_refundtimeout(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");

        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());
        InnerResultBO innerResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(innerResultBO).when(aliPayManager).refund(paymentDetailDO.getPaymentId(), paymentDetailDO.getTradeNo(),
                "null", refundDTO.getAmount().toString(), refundDTO.getDesc());
        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);

        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_REFUNDING);
        }
    }

    @Test
    public void recharge_refund_refundsuccess(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());
        InnerResultBO balanceResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), BalanceRemarkConstants.RECHARGE_REFUNDS);
        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(aliPayManager).refund(paymentDetailDO.getPaymentId(), paymentDetailDO.getTradeNo(),
                "null", refundDTO.getAmount().toString(), refundDTO.getDesc());
        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        RefundBO refundBO = refundService.refund(refundDTO);
        Assert.assertTrue(refundBO.isSuccessed());
    }

    @Test
    public void recharge_refund_refundingerror(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");
        InnerResultBO balanceResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(0).when(paymentDetailRepository).refunding(paymentDetailDO);
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance(paymentDetailDO.getPaymentId(),
                paymentDetailDO.getUserId(), OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(), refundDTO.getDesc());
        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_REFUNDING);
        }
    }

    @Test
    public void recharge_refund_refundtimeout(){
        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test12");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.ALI.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.RECHARGE.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        RefundDTO refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");
        InnerResultBO balanceResultBO = InnerResultBO.getTimeOutInstants();
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        Mockito.doReturn(balanceResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                OrderTypeEnum.getType(paymentDetailDO.getOrderType()).getBalanceType(),
                refundDTO.getAmount().negate(),
                BalanceRemarkConstants.RECHARGE_REFUNDS);
        RefundBO refundBO = refundService.refund(refundDTO);
        Assert.assertTrue(refundBO.isNoResponse());
    }
}
