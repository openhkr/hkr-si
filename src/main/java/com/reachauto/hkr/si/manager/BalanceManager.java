package com.reachauto.hkr.si.manager;

import com.reachauto.hkr.common.json.ReturnCode;
import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.exception.HkrBusinessException;
import com.reachauto.hkr.si.ErrCodeConstant;
import com.reachauto.hkr.si.constant.BalanceReturnCodeConstants;
import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.parameter.AccountModifyParameter;
import com.reachauto.hkr.si.pojo.vo.BalanceRecordStatusVO;
import com.reachauto.hkr.si.remote.CrBalanceRemote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author zhangshuo
 */
@Slf4j
@Component
public class BalanceManager {

    @Autowired
    CrBalanceRemote crBalanceRemote;

    public InnerResultBO modifyBalance(String requestId, String userId, Integer balanceType, BigDecimal amount, String remarks){
        AccountModifyParameter accountModifyParameter = new AccountModifyParameter();
        accountModifyParameter.setBalanceType(String.valueOf(balanceType));
        accountModifyParameter.setUuid(requestId);
        accountModifyParameter.setAmount(amount);
        accountModifyParameter.setRemarks(remarks);
        log.info("调用余额系统参数：{}", accountModifyParameter.toString());
        Response response = crBalanceRemote.modifyBalance(userId, accountModifyParameter);
        if(response == null){
            log.info("调用余额系统返回null");
            return InnerResultBO.getTimeOutInstants();
        }
        log.info("调用余额系统返回：{}，{}", response.getCode(), response.getDescription());
        if(response.getCode() == ReturnCode.SUCCESS || response.getCode() == BalanceReturnCodeConstants.BUSINESS_ALREADY_DONE){
            // 此处做幂等处理
            return InnerResultBO.getSuccessInstants();
        }else if(response.getCode() == BalanceReturnCodeConstants.BALANCE_ACCOUNT_NOT_ENOUGH){
            throw new HkrBusinessException(ErrCodeConstant.BALANCE_NOT_ENOUGH, "balance.not.enough");
        }else{
            return InnerResultBO.getFailInstants();
        }
    }

    /**
     * 查询支付记录状态
     * @param requestId
     * @return
     */
    public Integer recordStatusQuery(String requestId){
        Response<BalanceRecordStatusVO> response = crBalanceRemote.getRecordStatus(requestId);
        if(response == null || response.getCode() != ReturnCode.SUCCESS){
            log.error("查询余额支付记录{}失败", requestId);
            throw new HkrBusinessException();
        }
        return response.getPayload().getStatus();
    }

    public InnerResultBO rollbackModifyRecord(String requestId){
        Response rollbackResponse = crBalanceRemote.rollback(requestId);
        if(rollbackResponse == null){
            // 触发fegin的callback时按照超时处理
            return InnerResultBO.getTimeOutInstants();
        }else if(rollbackResponse.getCode() == ReturnCode.SUCCESS || rollbackResponse.getCode() == BalanceReturnCodeConstants.BUSINESS_ALREADY_DONE){
            return InnerResultBO.getSuccessInstants();
        }else{
            return InnerResultBO.getFailInstants();
        }
    }
}
