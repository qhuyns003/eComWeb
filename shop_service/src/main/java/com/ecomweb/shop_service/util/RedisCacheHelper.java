package com.ecomweb.shop_service.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisCacheHelper {

    JedisPool jedisPool;
    ObjectMapper objectMapper= new ObjectMapper();;

    public <T> T getFromCache(String key, Class<T> clazz) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String cachedData = jedis.get(key);
            if (cachedData != null) {
                return objectMapper.readValue(cachedData, clazz);
            }
            return null;
        }
    }

    public <T> void saveToCache(String key, T data, int ttl) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonData = objectMapper.writeValueAsString(data);
            jedis.setex(key, ttl, jsonData);
        }
    }
}
