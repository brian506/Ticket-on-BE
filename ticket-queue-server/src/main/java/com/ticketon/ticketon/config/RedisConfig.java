package com.ticketon.ticketon.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Value("${redis.waiting-line.host}")
    private String waitingHost;

    @Value("${redis.waiting-line.port}")
    private int waitingPort;

    @Value("${redis.reservation.host}")
    private String reservationHost;

    @Value("${redis.reservation.port}")
    private int reservationPort;

    // 대기열 ConnectionFactory
    @Bean
    public LettuceConnectionFactory waitingLettuceConnectionFactory() {
        return new LettuceConnectionFactory(waitingHost, waitingPort);
    }

    // 예약 (입장 여부) ConnectionFactory
    @Bean
    public LettuceConnectionFactory reservationLettuceConnectionFactory() {
        return new LettuceConnectionFactory(reservationHost, reservationPort);
    }

    // 기본 RedisTemplate (대기열 Redis)
    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, String> defaultRedisTemplate(
            @Qualifier("waitingLettuceConnectionFactory") LettuceConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 대기열 RedisTemplate
    @Bean
    public RedisTemplate<String, String> waitingRedisTemplate(
            @Qualifier("waitingLettuceConnectionFactory") LettuceConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 예약 RedisTemplate
    @Bean
    public RedisTemplate<String, String> reservationRedisTemplate(
            @Qualifier("reservationLettuceConnectionFactory") LettuceConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 대기열 ReactiveRedisTemplate
    @Bean
    public ReactiveRedisTemplate<String, String> waitingReactiveRedisTemplate(
            @Qualifier("waitingLettuceConnectionFactory") LettuceConnectionFactory factory) {
        RedisSerializationContext<String, String> context = RedisSerializationContext
                .<String, String>newSerializationContext(new StringRedisSerializer())
                .value(new StringRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
