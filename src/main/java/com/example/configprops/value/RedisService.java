package com.example.configprops.value;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;


    public void saveStrings(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

}
