package com.company.activity.redis;

public class ProductKey extends BasePrefix {
    private ProductKey(int expireSeconds, String prefix){super(expireSeconds, prefix);}
    public static ProductKey productList = new ProductKey(60, "pl");
}
