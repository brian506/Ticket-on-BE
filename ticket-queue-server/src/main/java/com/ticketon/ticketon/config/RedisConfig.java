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


    @Bean(destroyMethod = "shutdown")
    public RedisClient waitingRedisClient() {
        String redisUri = String.format("redis://%s:%d", waitingHost, waitingPort);
        return RedisClient.create(redisUri);
    }


    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> waitingStatefulRedisConnection(RedisClient waitingRedisClient) {
        return waitingRedisClient.connect();
    }

}
