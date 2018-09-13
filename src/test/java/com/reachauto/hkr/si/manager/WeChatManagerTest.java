package com.reachauto.hkr.si.manager;

import com.reachauto.hkr.si.pojo.bo.InnerResultBO;
import com.reachauto.hkr.si.pojo.bo.PaymentQueryBO;
import com.reachauto.hkr.si.pojo.enu.PaymentSourceEnum;
import com.reachauto.hkr.si.utils.GsonTool;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = SiApplication.class)
public class WeChatManagerTest {

    @Autowired
    private WeChatManager weChatManager;

    //@Test
    public void outTradeNo_weChatOrderquery_success() {
        PaymentQueryBO bo = weChatManager.weChatOrderQuery(PaymentSourceEnum.WECHAT, "2018012409040034261", "");
        System.out.println(GsonTool.objectToAllFieldNullJson(bo));
    }

    //@Test
    public void weChartUnifiedOrder_success() {
        String str = weChatManager.weChartUnifiedOrder(PaymentSourceEnum.WECHAT, "租车订单", "zs239847293847", new BigDecimal("1.00"), "93243c3eae20d974824f5ae47401dcdc", null);
        System.out.println(str);
    }

    //@Test
    public void weChatCloseOrder_success() {
        boolean ret = weChatManager.weChatCloseOrder(PaymentSourceEnum.WECHAT, "zs239847293847");
        System.out.println(ret);
    }

    //@Test
    public void weCharRefund_success() {
        InnerResultBO bo = weChatManager.weChatRefund(PaymentSourceEnum.WECHAT, "4A2018020213295061584", "",
                "103", BigDecimal.valueOf(1),
                BigDecimal.valueOf(1), "");
        System.out.println(bo);
    }
}