package com.reachauto.hkr.common.page;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/6/9.
 */
public class SortOrder {

    protected static final String KEYWORD_ORDER_BY = " ORDER BY ";

    protected List<Order> orders = new ArrayList<>();

    public SortOrder addAscOrder(String orderField) {
        this.orders.add(new Order(orderField, Order.KEYWORD_ASC));
        return this;
    }

    public SortOrder addDescOrder(String orderField) {
        this.orders.add(new Order(orderField, Order.KEYWORD_DESC));
        return this;
    }

    public SortOrder addSortOrder(SortOrder sortOrder) {

        if (sortOrder == null) {
            return this;
        }

        this.orders.addAll(sortOrder.orders);
        return this;

    }

    public String getOrderContent() {

        if (this.orders.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder(KEYWORD_ORDER_BY);
        int orderSize = 0;
        for (Order order : this.orders) {
            if (StringUtils.isEmpty(order.getOrderKey()) || StringUtils.isEmpty(order.getOrderType())) {
                continue;
            }
            result.append(order.getOrderKey()).append(order.getOrderType()).append(",");
            orderSize++;
        }

        if (orderSize < 1) {
            return "";
        } else {
            return result.substring(0, result.length() - 1);
        }

    }

    @Override
    public String toString() {
        return "SortOrder{" +
                "orders=" + getOrderContent() +
                '}';
    }

}
