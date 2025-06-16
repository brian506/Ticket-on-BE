package com.ticketon.ticketon.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean(name = "queueRedisConnectionFactory")
    public LettuceConnectionFactory queueRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }


    @Bean(name = "queueRedisTemplate")
    public RedisTemplate<String, Object> queueRedisTemplate(
            @Qualifier("queueRedisConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }
}
