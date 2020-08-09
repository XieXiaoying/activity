package com.company.activity.redis;

public class ActivityKey extends BasePrefix {
    public ActivityKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static ActivityKey getVerifyCode = new ActivityKey(300, "vc");
    public static ActivityKey getRegisterVerifyCode = new ActivityKey(300, "register");
    public static ActivityKey getActivityPath = new ActivityKey(60, "path");
    public static ActivityKey getProductOver = new ActivityKey(0, "over");

}
