package com.reachauto.hkr.si.pojo.result;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * @author zhangshuo
 */
@Data
@XStreamAlias("xml")
public class WechatCloseOrderResult {

    /**
     * 微信关闭接口文档 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_3&index=5
     */
    @XStreamAlias("return_code")
    private String returnCode;

    @XStreamAlias("return_msg")
    private String returnMsg;

    @XStreamAlias("appid")
    private String appid;

    @XStreamAlias("mch_id")
    private String mchId;

    @XStreamAlias("nonce_str")
    private String nonceStr;

    @XStreamAlias("sign")
    private String sign;

    @XStreamAlias("result_code")
    private String resultCode;

    @XStreamAlias("result_msg")
    private String resultMsg;

    @XStreamAlias("err_code")
    private String errCode;

    @XStreamAlias("err_code_des")
    private String errCodeDes;
}
