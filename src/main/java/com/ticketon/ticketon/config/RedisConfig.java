package com.ticketon.ticketon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 로컬 Redis: 기본(host=localhost, port=6379)
        return new LettuceConnectionFactory();
    }

    @Bean
    public StringRedisTemplate redisTemplate(LettuceConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }

}
