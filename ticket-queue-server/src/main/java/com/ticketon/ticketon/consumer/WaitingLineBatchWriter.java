package com.ticketon.ticketon.consumer;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WaitingLineBatchWriter {

    private final StatefulRedisConnection<String, String> connection;
    private final RedisAsyncCommands<String, String> asyncCommands;
    private final Sinks.Many<String> sink;

    private static final String WAITING_LINE = "waiting-line";

    public WaitingLineBatchWriter(StatefulRedisConnection<String, String> connection) {
        this.connection = connection;
        this.asyncCommands = connection.async();
        this.asyncCommands.setAutoFlushCommands(false);
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
        startFlushLoop();
    }

    public void enqueue(String email) {
        sink.tryEmitNext(email);
    }

    private void startFlushLoop() {
        sink.asFlux()
                .bufferTimeout(5000, Duration.ofMillis(20))
                .flatMap(this::writeBatchToRedis)
                .onErrorContinue((e, o) -> log.error("Flush 에러", e))
                .subscribe();
    }

    private Mono<Void> writeBatchToRedis(List<String> emails) {
        return Mono.fromCallable(() -> {
            List<io.lettuce.core.RedisFuture<?>> futures = new ArrayList<>();
            double now = System.currentTimeMillis();
            for (String email : emails) {
                futures.add(asyncCommands.zadd(WAITING_LINE, now, email));
                futures.add(asyncCommands.setex("ttl:waiting:" + email, 60 * 20, "1"));
            }
            asyncCommands.flushCommands(); // 한번에 전송
            return futures;
        }).flatMap(futures ->
                Flux.fromIterable(futures)
                        .flatMap(future -> Mono.fromFuture(future.toCompletableFuture()))
                        .then()
        );
    }
}
