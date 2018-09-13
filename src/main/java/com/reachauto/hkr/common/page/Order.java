package com.reachauto.hkr.common.page;

import org.springframework.util.StringUtils;

/**
 * Created by Administrator on 2017/6/12.
 */
class Order {

    protected static final String KEYWORD_ASC = " ASC ";

    protected static final String KEYWORD_DESC = " DESC ";

    private String orderKey;

    private String orderType;

    Order(String orderKey, String orderType) {
        if (StringUtils.isEmpty(orderKey) || StringUtils.isEmpty(orderType)) {
            return;
        }
        this.orderKey = orderKey;
        this.orderType = orderType;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public String getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderKey='" + orderKey + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }

}
