package com.company.activity.redis.redismanager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private static JedisPool jedisPool;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPool = new JedisPool("10.225.1.54",30449);
    }

    public static Jedis getJedis() throws Exception{
        if(null!=jedisPool){
            return jedisPool.getResource();
        }
        throw new Exception("Jedispool was not init !!!");
    }


}