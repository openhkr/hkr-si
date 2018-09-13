package com.tencent.common;

import com.tencent.protocol.refund_query_protocol.RefundOrderData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * User: rizenguo
 * Date: 2014/11/1
 * Time: 14:06
 */
public class XMLParser {

    /**
     * 从RefunQueryResponseString里面解析出退款订单数据
     *
     * @param refundQueryResponseString RefundQuery API返回的数据
     * @return 因为订单数据有可能是多个，所以返回一个列表
     */
    public static List<RefundOrderData> getRefundOrderList(String refundQueryResponseString) throws IOException, SAXException, ParserConfigurationException {
        List list = new ArrayList();

        Map<String, Object> map = XMLParser.getMapFromXML(refundQueryResponseString);

        int count = Integer.parseInt((String) map.get("refund_count"));
        Util.log("count:" + count);

        if (count < 1) {
            return list;
        }

        RefundOrderData refundOrderData;

        for (int i = 0; i < count; i++) {
            refundOrderData = new RefundOrderData();

            refundOrderData.setOutRefundNo(Util.getStringFromMap(map, "out_refund_no_" + i, ""));
            refundOrderData.setRefundID(Util.getStringFromMap(map, "refund_id_" + i, ""));
            refundOrderData.setRefundChannel(Util.getStringFromMap(map, "refund_channel_" + i, ""));
            refundOrderData.setRefundFee(Util.getIntFromMap(map, "refund_fee_" + i));
            refundOrderData.setCouponRefundFee(Util.getIntFromMap(map, "coupon_refund_fee_" + i));
            refundOrderData.setRefundStatus(Util.getStringFromMap(map, "refund_status_" + i, ""));
            list.add(refundOrderData);
        }

        return list;
    }

    public static Map<String, Object> getMapFromXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {

        //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = Util.getStringStream(xmlString);
        Document document = builder.parse(is);

        //获取到document里面的全部结点
        NodeList allNodes = document.getFirstChild().getChildNodes();
        Node node;
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        while (i < allNodes.getLength()) {
            node = allNodes.item(i);
            if (node instanceof Element) {
                map.put(node.getNodeName(), node.getTextContent());
            }
            i++;
        }
        return map;

    }

    /**
     * 解析统一下单请求返还响应，转换成json格式字符串
     *
     * @param unifiedOrderResStr 统一下单请求返还响应
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws IllegalAccessException
     */
    public static String getUnifiedOrderResData(String unifiedOrderResStr) throws ParserConfigurationException, SAXException, IOException, IllegalAccessException {
        String jsonstr = "{";
        Map<String, Object> map = XMLParser.getMapFromXML(unifiedOrderResStr);//这里数据受网络限制，内网无法获取数据
        String return_code = Util.getStringFromMap(map, "return_code", "");
        String return_msg = Util.getStringFromMap(map, "return_msg", "");
        String appid = Util.getStringFromMap(map, "appid", "");
        String mch_id = Util.getStringFromMap(map, "mch_id", "");
        String nonce_str = Util.getStringFromMap(map, "nonce_str", "");
//		String sign  = Util.getStringFromMap(map, "sign", "");
        String result_code = Util.getStringFromMap(map, "result_code", "");
        String prepay_id = Util.getStringFromMap(map, "prepay_id", "");
        String trade_type = Util.getStringFromMap(map, "trade_type", "");
        //参与签名的参数 appid、mch_id、prepay_id、nonce_str、timestamp、package
        String timestamp = String.valueOf((new Date()).getTime() / 1000);//时间戳
        Map<String, Object> resmap = new HashMap<String, Object>();
        resmap.put("return_code", return_code);
        resmap.put("return_msg", return_msg);
        resmap.put("appid", appid);
        resmap.put("noncestr", nonce_str);
        resmap.put("package", "Sign=WechatPay");
        resmap.put("partnerid", mch_id);
        resmap.put("prepayid", prepay_id);
        resmap.put("timestamp", timestamp);
        //返回给客户端的sign要根据客户端发起支付请求时要提交的参数重新签名
        String responseSign = Signature.getSign(resmap);
        resmap.put("sign", responseSign);
        jsonstr = jsonstr + "\"return_code\":\"" + return_code + "\",\"return_msg\":\"" + return_msg + "\",\"appid\":\"" + appid + "\",\"mch_id\":\"" + mch_id + "\",\"nonce_str\":\"" +
                nonce_str + "\",\"timestamp\":\"" + timestamp + "\",\"sign\":\"" + responseSign + "\",\"result_code\":\"" + result_code + "\",\"prepay_id\":\"" + prepay_id + "\",\"trade_type\":\"" + trade_type + "\"}";
        return jsonstr;
    }

}
