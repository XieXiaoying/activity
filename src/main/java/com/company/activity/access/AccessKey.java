package com.company.activity.access;

import com.company.activity.redis.BasePrefix;

public class AccessKey extends BasePrefix {

    private AccessKey( int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }

}