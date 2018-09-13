package com.reachauto.hkr.si.remote;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.si.pojo.parameter.AccountModifyParameter;
import com.reachauto.hkr.si.pojo.vo.BalanceRecordStatusVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author zhangshuo
 */
public interface CrBalanceRemote {

//    @RequestMapping(value = "/balances/user_id={userId}", method = RequestMethod.PUT, headers = "api-version=1")
    Response modifyBalance(@PathVariable("userId") String userId, @RequestBody @Valid AccountModifyParameter accountModifyParameter);

//    @RequestMapping(value = "/balances/rollback/{uuid}", method = RequestMethod.PUT, headers = "api-version=1")
    Response rollback(@PathVariable("uuid") String uuid);

//    @RequestMapping(value = "/balances/{uuid}/record_status", method = RequestMethod.GET, headers = "api-version=1")
    Response<BalanceRecordStatusVO> getRecordStatus(@PathVariable("uuid") String uuid);
}
