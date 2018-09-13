package com.tencent.protocol;

/**
 * Created by Administrator on 2017/3/13.
 */
public class AppInfo {

    //签名算法要用到的API密钥
    private String key;

    //微信AppID
    private String appID;

    //微信支付分配的商户号ID
    private String mchID;

    //受理模式下给子商户分配的子商户号
    private String subMchID = "";

    private String tradeType;

    public AppInfo() {
        super();
    }

    public AppInfo(String key, String appID, String mchID, String subMchID, String tradeType) {
        this.key = key;
        this.appID = appID;
        this.mchID = mchID;
        this.subMchID = subMchID;
        this.tradeType = tradeType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public String getSubMchID() {
        return subMchID;
    }

    public void setSubMchID(String subMchID) {
        this.subMchID = subMchID;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
