package com.qhuyns.ecomweb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
@Configuration
public class RedisConfig {
// redis luudu lieu kieu key-value
    // key mac dinh la string
    // value co nhieu ctdl ho tro nhu String List Set Map
    // thuong luu String (JSON)

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setJmxEnabled(false); // tắt JMX
        return new JedisPool(poolConfig, "localhost", 6379);
    }
}


