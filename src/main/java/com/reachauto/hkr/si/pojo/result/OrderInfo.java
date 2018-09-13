package com.reachauto.hkr.si.pojo.result;

import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.utils.wechat.Signature;
import com.tencent.common.RandomStringGenerator;
import com.tencent.protocol.AppInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/3.
 */
@Data
@ApiModel
public class OrderInfo {

    @ApiModelProperty(position = 1, required = false, value = "实际需要支付金额")
    private String actualPayment;

    @ApiModelProperty(position = 2, required = false, value = "微信支付 appid ")
    private String appid;

    @ApiModelProperty(position = 3, required = false, value = "微信支付 商户id")
    private String mchId;

    @ApiModelProperty(position = 4, required = false, value = "随机字符串, 用以混淆参数签名")
    private String nonceStr;

    @ApiModelProperty(position = 5, required = true, value = "微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时，此值与支付流水号不同，支付流水号为微信回调时提供")
    private String prepayId;

    @ApiModelProperty(position = 6, required = true, value = "氢氪数据库支付表,支付流水号")
    private String paymentId;

    @ApiModelProperty(position = 7, required = false, value = "响应 签名")
    private String responseSign;

    @ApiModelProperty(position = 8, required = false, value = "微信支付 时间戳")
    private long timestamp;

    public OrderInfo(JSONObject jsonObject, AppInfo appInfo, PaymentSourceEnum paymentSource) {

        this.prepayId = jsonObject.getString("prepay_id");
        // 重新生成，随机字符串
        this.nonceStr = RandomStringGenerator.getRandomStringByLength(32);

        this.responseSign = "";
        // 时间戳, 单位是秒
        this.timestamp = (new Date()).getTime() / 1000;

        this.appid = appInfo.getAppID();
        this.mchId = appInfo.getMchID();

        this.responseSign = getAppSign(appInfo.getKey());
    }

    /**
     * 获取APP签名
     * @param signKey
     * @return
     */
    private String getAppSign(String signKey){
        Map<String, Object> map = new HashMap<>(8);
        map.put("appid", appid);
        map.put("partnerid", mchId);
        map.put("prepayid", prepayId);
        map.put("package", "Sign=WXPay");
        map.put("timestamp", timestamp);
        map.put("noncestr", nonceStr);
        return Signature.getSign(map, signKey);
    }
}