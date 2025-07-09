package com.ticketon.ticketon.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
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

    // 대기열 LettuceConnectionFactory
    @Bean
    public LettuceConnectionFactory waitingLettuceConnectionFactory() {
        return new LettuceConnectionFactory(waitingHost, waitingPort);
    }

    // 예약 LettuceConnectionFactory
    @Bean
    public LettuceConnectionFactory reservationLettuceConnectionFactory() {
        return new LettuceConnectionFactory(reservationHost, reservationPort);
    }

    // RedisClient (Lettuce) — 대기열용 (StatefulRedisConnection 생성용)
    @Bean(destroyMethod = "shutdown")
    public RedisClient waitingRedisClient() {
        String redisUri = String.format("redis://%s:%d", waitingHost, waitingPort);
        return RedisClient.create(redisUri);
    }

    // StatefulRedisConnection 빈 생성 (RedisClient에서 직접 connect)
    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> waitingStatefulRedisConnection(RedisClient waitingRedisClient) {
        return waitingRedisClient.connect();
    }

    // 기본 RedisTemplate (대기열 Redis)
    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, String> defaultRedisTemplate(
            LettuceConnectionFactory waitingLettuceConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(waitingLettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 대기열 RedisTemplate
    @Bean
    public RedisTemplate<String, String> waitingRedisTemplate(
            LettuceConnectionFactory waitingLettuceConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(waitingLettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 예약 RedisTemplate
    @Bean
    public RedisTemplate<String, String> reservationRedisTemplate(
            LettuceConnectionFactory reservationLettuceConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(reservationLettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // 대기열 ReactiveRedisTemplate
    @Bean
    public ReactiveRedisTemplate<String, String> waitingReactiveRedisTemplate(
            LettuceConnectionFactory waitingLettuceConnectionFactory) {
        RedisSerializationContext<String, String> context = RedisSerializationContext
                .<String, String>newSerializationContext(new StringRedisSerializer())
                .value(new StringRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(waitingLettuceConnectionFactory, context);
    }
}
