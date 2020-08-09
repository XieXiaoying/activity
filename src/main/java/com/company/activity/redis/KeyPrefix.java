package com.company.activity.redis;

public interface KeyPrefix {

    public int expireSeconds() ;

    public String getPrefix() ;

}