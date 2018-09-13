package com.tencent.service;


import com.tencent.common.Configure;
import com.tencent.protocol.pay_protocol.UnifiedOrderReqData;

/**
 * 预付单请求服务
 */

public class UnfiedOrderService extends BaseService {

    public UnfiedOrderService() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        super(Configure.UNIFIEDORDER_API);
    }

    /**
     * 请求统一下单API
     *
     * @param unifiedorderReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public String request(UnifiedOrderReqData unifiedorderReqData) throws Exception {

        //--------------------------------------------------------------------
        //发送HTTPS的Post请求到API地址
        //--------------------------------------------------------------------
        String responseString = sendPost(unifiedorderReqData);

        return responseString;
    }
}
