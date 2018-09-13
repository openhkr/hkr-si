package com.reachauto.hkr.si.config;

import com.reachauto.hkr.si.constant.WeChatConstant;
import com.tencent.protocol.AppInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信后端配置数据
 */
@Configuration
public class WechatConfigure {

    /**
     * 每次自己Post数据给API的时候都要用这个key来对所有字段进行签名，生成的签名会放在Sign这个字段，API收到Post数据的时候也会用同样的签名算法对Post过来的数据进行签名和验证
     * 收到API的返回的时候也要用这个key来对返回的数据算下签名，跟API的Sign数据进行比较，如果值不一致，有可能数据被第三方给篡改
     * 签名算法要用到的API密钥
     */
    public static final String KEY = "";

    /**
     * 微信AppID
     */
    public static final String APP_ID = "";

    /**
     * 微信支付分配的商户号ID
     */
    public static final String MCH_ID = "";

    /**
     * 受理模式下给子商户分配的子商户号
     */
    public static final String SUB_MCH_ID = "";

    /**
     * HTTPS证书密码，默认密码等于商户号MCHID
     */
    public static final String CERT_PASS = "";

    public static final String CERT_PATH = "";

    /**
     * 是否使用异步线程的方式来上报API测速，默认为异步模式
     */
    private static boolean useThreadToDoReport = true;

    /**
     *  以下是几个API的路径：
     *  微信APP和微信公众号共用此路径
     */
    /**
     * 1）被扫支付API
     */
    public static final String PAY_API = "https://api.mch.weixin.qq.com/pay/micropay";

    /**
     * 2）被扫支付查询API
     */
    public static final String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 3）退款API
     */
    public static final String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * 4）退款查询API
     */
    public static final String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

    /**
     * 5）撤销API
     */
    public static final String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

    /**
     * 6）下载对账单API
     */
    public static final String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

    /**
     * 7) 统计上报API
     */
    public static final String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";

    /**
     * 8) 统一下单API
     */
    public static final String UNIFIEDORDER_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 9) 关闭订单API
     */
    public static final String CLOSEORDER_API = "https://api.mch.weixin.qq.com/pay/closeorder";

    /**
     * 10) 发放代金券API
     */
    public static final String COUPON_STOCK_API = "https://api.mch.weixin.qq.com/mmpaymkttransfers/send_coupon";

    public static boolean isUseThreadToDoReport() {
        return useThreadToDoReport;
    }

    public static void setUseThreadToDoReport(boolean useThreadToDoReport) {
        WechatConfigure.useThreadToDoReport = useThreadToDoReport;
    }

    @Bean(name = "wechatAppInfo")
    public AppInfo wechatAppInfo() {
        return new AppInfo(
                KEY,
                APP_ID,
                MCH_ID,
                SUB_MCH_ID,
                WeChatConstant.WECHAT_PAY_NAME
        );
    }
}
