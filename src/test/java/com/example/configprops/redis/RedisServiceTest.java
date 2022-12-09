package com.example.configprops.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void setKeyValueTest() {

        String key = "myKey";
        String value = "success";
        redisService.saveStrings(key, value);

        String findValue = redisTemplate.opsForValue().get(key);

        assertThat(value).isEqualTo(findValue);
    }

}
