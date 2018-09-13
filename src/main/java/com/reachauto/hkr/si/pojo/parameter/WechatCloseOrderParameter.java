package com.reachauto.hkr.si.pojo.parameter;

import com.reachauto.hkr.si.utils.GsonTool;
import com.reachauto.hkr.si.utils.wechat.Signature;
import com.tencent.common.RandomStringGenerator;
import com.tencent.protocol.AppInfo;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * @author zhangshuo
 */
@Data
@XStreamAlias("xml")
public class WechatCloseOrderParameter {

    private String appid = "";
    private String mch_id = "";
    private String out_trade_no = "";
    private String nonce_str = "";
    private String sign = "";

    public WechatCloseOrderParameter(AppInfo appInfo, String outTradeNo) {
        this.appid = appInfo.getAppID();
        this.mch_id = appInfo.getMchID();
        this.out_trade_no = outTradeNo;
        this.nonce_str = RandomStringGenerator.getRandomStringByLength(32);

        String json = GsonTool.objectToAllFieldEmptyJson(this);
        // 签名之前sign参数必须为空，此时它不参与签名
        this.sign = Signature.getSign(GsonTool.jsonToMap(json), appInfo.getKey());
    }
}
