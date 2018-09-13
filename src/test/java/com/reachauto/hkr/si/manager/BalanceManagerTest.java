package com.reachauto.hkr.si.manager;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.common.response.ResponseHelper;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.constant.BalanceReturnCodeConstants;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.vo.BalanceRecordStatusVO;
import com.reachauto.hkr.si.remote.CrBalanceRemote;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

public class BalanceManagerTest {

    @InjectMocks
    private BalanceManager manager = new BalanceManager();

    @Mock
    private CrBalanceRemote crBalanceRemote;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void modifyBalance_success(){
        Mockito.doReturn(ResponseHelper.createSuccessResponse()).when(crBalanceRemote).modifyBalance(Mockito.any(), Mockito.any());
        InnerResultBO innerResultBO = manager.modifyBalance("1321", "123", 16,
                new BigDecimal("1.00"), "12wdw");
        Assert.assertTrue(innerResultBO.isSuccessed());
    }

    @Test
    public void modifyBalance_timeout(){
        Mockito.doReturn(null).when(crBalanceRemote).modifyBalance(Mockito.any(), Mockito.any());
        InnerResultBO innerResultBO = manager.modifyBalance("1321", "123", 16,
                new BigDecimal("1.00"), "12wdw");
        Assert.assertTrue(innerResultBO.isTimeOut());
    }

    @Test
    public void modifyBalance_balance_not_enough(){
        Response response = ResponseHelper.createSuccessResponse();
        response.setCode(BalanceReturnCodeConstants.BALANCE_ACCOUNT_NOT_ENOUGH);
        Mockito.doReturn(response).when(crBalanceRemote).modifyBalance(Mockito.any(), Mockito.any());
        thrown.expect(HkrBusinessException.class);
        thrown.expectMessage("balance.not.enough");
        InnerResultBO innerResultBO = manager.modifyBalance("1321", "123", 16,
                new BigDecimal("1.00"), "12wdw");
    }

    @Test
    public void modifyBalance_failed(){
        Response response = ResponseHelper.createSuccessResponse();
        response.setCode(123);
        Mockito.doReturn(response).when(crBalanceRemote).modifyBalance(Mockito.any(), Mockito.any());
        InnerResultBO innerResultBO = manager.modifyBalance("1321", "123", 16,
                new BigDecimal("1.00"), "12wdw");
        Assert.assertTrue(innerResultBO.isFailed());
    }

    @Test
    public void recordStatusQuery_success(){
        BalanceRecordStatusVO vo = new BalanceRecordStatusVO();
        vo.setStatus(1);
        Mockito.doReturn(ResponseHelper.createSuccessResponse(vo)).when(crBalanceRemote).getRecordStatus("123");
        Integer result = manager.recordStatusQuery("123");
        Assert.assertEquals(1, result.intValue());
    }

    @Test
    public void recordStatusQuery_null(){
        Mockito.doReturn(null).when(crBalanceRemote).getRecordStatus("123");
        thrown.expect(HkrBusinessException.class);
        Integer result = manager.recordStatusQuery("123");
    }

    @Test
    public void rollbackModifyRecord_success(){
        Mockito.doReturn(ResponseHelper.createSuccessResponse()).when(crBalanceRemote).rollback("123");
        InnerResultBO innerResultBO = manager.rollbackModifyRecord("123");
        Assert.assertTrue(innerResultBO.isSuccessed());
    }

    @Test
    public void rollbackModifyRecord_timeout(){
        Mockito.doReturn(null).when(crBalanceRemote).rollback("123");
        InnerResultBO innerResultBO = manager.rollbackModifyRecord("123");
        Assert.assertTrue(innerResultBO.isTimeOut());
    }

    @Test
    public void rollbackModifyRecord_failed(){
        Mockito.doReturn(ResponseHelper.createNotFoundResponse()).when(crBalanceRemote).rollback("123");
        InnerResultBO innerResultBO = manager.rollbackModifyRecord("123");
        Assert.assertTrue(innerResultBO.isFailed());
    }
}
