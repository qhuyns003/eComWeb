package com.qhuyns.ecomweb.configuration;

import redis.clients.jedis.Jedis;

public class RedisConfig {
    private static Jedis jedis;

    static {
        jedis = new Jedis("localhost", 6379); 
        jedis.connect();
    }

    public static Jedis getRedisClient() {
        return jedis;
    }
}
