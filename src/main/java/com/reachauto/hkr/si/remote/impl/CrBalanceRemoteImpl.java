package com.reachauto.hkr.si.remote.impl;

import com.reachauto.hkr.common.response.Response;
import com.reachauto.hkr.si.pojo.parameter.AccountModifyParameter;
import com.reachauto.hkr.si.pojo.vo.BalanceRecordStatusVO;
import com.reachauto.hkr.si.remote.CrBalanceRemote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangshuo
 *
 * 1.timeout是触发callback的原因之一
 * 2.当触发callback时，可以当成超时处理，可保证流程正常
 */
@Slf4j
@Component
public class CrBalanceRemoteImpl implements CrBalanceRemote {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cr.host}")
    private String crHost;

    @Override
    public Response modifyBalance(String userId, AccountModifyParameter accountModifyParameter) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-version", "1");
        HttpEntity<AccountModifyParameter> entity = new HttpEntity<>(accountModifyParameter, headers);
        String url = crHost + "/api/v2/balances/user_id=" + userId;
        ResponseEntity<Response> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity, Response.class);
        return responseEntity.getBody();
    }

    @Override
    public Response rollback(String uuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-version", "1");
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);
        String url = crHost + "/api/v2/balances/rollback/" + uuid;
        ResponseEntity<Response> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity, Response.class);
        return responseEntity.getBody();
    }

    @Override
    public Response<BalanceRecordStatusVO> getRecordStatus(String uuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-version", "1");
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);
        String url = crHost + "/api/v2/balances/" + uuid + "/record_status";
        ResponseEntity<Response<BalanceRecordStatusVO>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Response<BalanceRecordStatusVO>>(){});
        return responseEntity.getBody();
    }
}
