package com.company.activity.redis;

public class OrderKey extends BasePrefix {
    public OrderKey(String prefix){super(prefix);}
    public static OrderKey orderKeyByUserIdAndProductsId = new OrderKey("orderKey");
}
