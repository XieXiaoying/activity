package com.company.activity.redis.redismanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * lua脚本使用
 */
public class RedisScript {

    private static Logger logger = LoggerFactory.getLogger(RedisScript.class);

    /**
     * 未完成  需 evalsha更方便 限制ip 或者 手机号访问次数
     */
    public static void getLuaLimit() {

        Jedis jedis = null;
        try {
            jedis = RedisFactory.getJedis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String lua =
                "local num=redis.call('incr',KEYS[1]) if tonumber(num)==1 " +
                        "then redis.call('expire',KEYS[1],ARGV[1]) " +
                        "return 1 elseif tonumber(num)>" +
                        "tonumber(ARGV[2]) then return 0 else return 1 end";

        List<String> keys = new ArrayList<String>();
        keys.add("ip:limit:127.0.0.1");
        List<String> argves = new ArrayList<String>();
        argves.add("6000");
        argves.add("5");
        jedis.auth("xxxx");
//        Object evalSha = jedis.evalsha(lua);
        String luaScript = jedis.scriptLoad(lua);
        System.out.println(luaScript);
        Object object = jedis.evalsha(luaScript, keys, argves);
        System.out.println(object);
    }

    /**
     * 统计访问次数
     */
    public static Object getVisitCountByUserKey(String key) {

        Jedis jedis = null;
        Object object = null;
        try {
            jedis = RedisFactory.getJedis();
            String count = "local count = redis.call('get',KEYS[1]) return count";
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> argves = new ArrayList<String>();
            jedis.auth("1qaz@WSX");
            String luaScript = jedis.scriptLoad(count);
            object = jedis.evalsha(luaScript, keys, argves);
        } catch (Exception e) {
            logger.error("统计访问次数失败！！！",e);
            return "0";
        }
        return  object;
    }

    /**
     * 统计访问次数
     */
    public static void addCountByUserKey(String key) {

        Jedis jedis = null;
        Object object = null;
        try {
            jedis = RedisFactory.getJedis();
            String count = "local count = redis.call('incr',KEYS[1]) return count";
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> argves = new ArrayList<String>();
            jedis.auth("1qaz@WSX");
            String luaScript = jedis.scriptLoad(count);
            jedis.evalsha(luaScript, keys, argves);
        } catch (Exception e) {
            logger.error("统计访问次数失败！！！",e);
        }
    }
}
