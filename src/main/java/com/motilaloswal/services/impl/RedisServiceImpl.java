package com.motilaloswal.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motilaloswal.dto.cfFullResponse.CfFullDataResponse;
import com.motilaloswal.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public <T> T get(String key, ParameterizedTypeReference<T> entityClass){
        try {
            Object o = redisTemplate.opsForValue().get(key);
            if(o == null) return null;
            ObjectMapper objectMapper = new ObjectMapper();
            Type type = entityClass.getType();
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            return objectMapper.readValue(o.toString(), javaType);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON from Redis for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    @Override
    public void set(String key, Object o, Long ttl){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error setting value in Redis for key {}: {}", key, e.getMessage());
        }
    }
}
