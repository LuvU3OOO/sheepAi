package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {


    @Resource
    StringRedisTemplate stringRedisTemplate;

    // 验证redis 是否有email数据，防止一直重复发送
    public boolean limitOnceCheck(String key,int blocktime) {
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            return false;
        } else {
            stringRedisTemplate.opsForValue().set(key,"",blocktime, TimeUnit.SECONDS);
            return true;
        }
    }
}
