package com.ticketon.ticketon.consumer;

import com.ticketon.ticketon.dto.EnqueuedUser;
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
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class WaitingLineBatchWriter {

    private final RedisAsyncCommands<String, String> asyncCommands;
    private final Sinks.Many<EnqueuedUser> sink;

    private static final String WAITING_LINE = "waiting-line";
    private final AtomicLong sequence = new AtomicLong();

    public WaitingLineBatchWriter(StatefulRedisConnection<String, String> connection) {
        this.asyncCommands = connection.async();
        this.asyncCommands.setAutoFlushCommands(false);
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
        startFlushLoop();
    }

    public void enqueue(String email) {
        long timestamp = System.currentTimeMillis();
        long offset = sequence.getAndIncrement(); // 동일 시간 내에 순서 보장
        double score = timestamp + (offset * 0.0001); // 밀리초보다 미세하게 차이 줌
        sink.tryEmitNext(new EnqueuedUser(email, score));
    }

    private void startFlushLoop() {
        sink.asFlux()
                .bufferTimeout(5000, Duration.ofMillis(20))
                .flatMap(this::writeBatchToRedis)
                .onErrorContinue((e, o) -> log.error("Flush 에러", e))
                .subscribe();
    }

    private Mono<Void> writeBatchToRedis(List<EnqueuedUser> users) {
        return Mono.fromCallable(() -> {
            List<io.lettuce.core.RedisFuture<?>> futures = new ArrayList<>();
            for (EnqueuedUser user : users) {
                futures.add(asyncCommands.zadd(WAITING_LINE, user.getScore(), user.getEmail()));
                futures.add(asyncCommands.setex("ttl:waiting:" + user.getEmail(), 60 * 20, "1"));
            }
            asyncCommands.flushCommands();
            return futures;
        }).flatMap(futures ->
                Flux.fromIterable(futures)
                        .flatMap(future -> Mono.fromFuture(future.toCompletableFuture()))
                        .then()
        );
    }
}
