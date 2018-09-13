package com.reachauto.hkr.si.service.impl;

import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.entity.PaymentDetailDO;
import com.reachauto.hkr.si.manager.BalanceManager;
import com.reachauto.hkr.si.persistence.PaymentDetailRepository;
import com.reachauto.hkr.si.persistence.RefundRecordRepository;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.RefundBO;
import com.reachauto.hkr.si.pojo.dto.RefundDTO;
import com.reachauto.hkr.si.pojo.enu.OrderTypeEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.pojo.enu.PaymentTypeEnum;
import com.reachauto.hkr.si.pojo.enu.TradeStatusEnum;
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
 * @create 2018-02-26 14:03
 * 单元测试说明，共测试以下场景：
 * 余额支付退款
 *  --退款成功
 *  --退款参数校验
 *  --退款进行中异常
 *  --超时异常
 */

public class BalanceRefundServiceImplTest {

    @InjectMocks
    private BalanceRefundServiceImpl refundService = new BalanceRefundServiceImpl();

    @Mock
    protected PaymentDetailRepository paymentDetailRepository;

    @Mock
    protected BalanceManager balanceManager;

    @Mock
    protected RefundRecordRepository refundRecordRepository;

    private RefundDTO refundDTO;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
        paymentDetailDO.setPaymentId("1");
        paymentDetailDO.setUserId("test");
        paymentDetailDO.setTradeTotalFee(BigDecimal.valueOf(1));
        paymentDetailDO.setOrderType(OrderTypeEnum.RENTAL.getCode());
        paymentDetailDO.setPaymentSource(PaymentSourceEnum.COMMON_BALANCE.getCode());
        paymentDetailDO.setPaymentType(PaymentTypeEnum.PAYMENT.getCode());
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_SUCCESS.getCode());
        refundDTO = new RefundDTO(paymentDetailDO, BigDecimal.valueOf(1), "1");
    }

    @Test
    public void pay_refund_refundsuccess(){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        // 修改退款中成功
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        // 创建退款记录
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        RefundBO refundBO = refundService.refund(refundDTO);
        Assert.assertTrue(refundBO.isSuccessed());
    }

    @Test
    public void pay_refund_refunding(){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDING.getCode());
        // 修改退款中成功
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        // 创建退款记录
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_REFUNDING);
        }
    }

    @Test
    public void pay_refund_refunded(){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_REFUNDED.getCode());
        // 修改退款中成功
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        // 创建退款记录
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_HAS_REF);
        }
    }

    @Test
    public void pay_refund_refundtypeerror(){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        paymentDetailDO.setTradeStatus(TradeStatusEnum.TRADE_FAIL.getCode());
        // 修改退款中成功
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        // 创建退款记录
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.ORDER_CANNOT_REFUND);
        }
    }

    @Test
    public void pay_refund_refundamouterror(){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        refundDTO.setAmount(BigDecimal.valueOf(2));
        // 修改退款中成功
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        // 创建退款记录
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.AMOUNT_ERROR);
        }
    }

    @Test
    public void pay_refund_refundbalancefailed(){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        // 修改退款中成功
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        // 创建退款记录
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getFailInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        Mockito.doReturn(1).when(paymentDetailRepository).refunded(paymentDetailDO);
        try{
            RefundBO refundBO = refundService.refund(refundDTO);
        }catch (Exception e){
            Assert.assertTrue(e instanceof HkrBusinessException);
            Assert.assertEquals(((HkrBusinessException) e).getCode(), ErrCodeConstant.REFUND_FAILED);
        }
    }

    @Test
    public void pay_refund_refundresultexceptionsuccess(){
        PaymentDetailDO paymentDetailDO = refundDTO.getPaymentDetailDO();
        // 修改退款中成功
        Mockito.doReturn(1).when(paymentDetailRepository).refunding(paymentDetailDO);
        // 创建退款记录
        Mockito.doNothing().when(refundRecordRepository).create(Mockito.any());

        InnerResultBO innerResultBO = InnerResultBO.getSuccessInstants();
        Mockito.doReturn(innerResultBO).when(balanceManager).modifyBalance("null",
                paymentDetailDO.getUserId(),
                PaymentSourceEnum.getType(paymentDetailDO.getPaymentSource().intValue()).getBalanceType(),
                refundDTO.getAmount(), refundDTO.getDesc());

        Mockito.doThrow(new HkrBusinessException()).when(paymentDetailRepository).refunded(paymentDetailDO);
        RefundBO refundBO = refundService.refund(refundDTO);
        Assert.assertTrue(refundBO.isSuccessed());
    }
}
