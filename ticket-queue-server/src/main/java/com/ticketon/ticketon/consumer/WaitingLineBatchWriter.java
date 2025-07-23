package com.ticketon.ticketon.consumer;

import com.ticket.exception.custom.WaitingLineRedisFlushException;
import com.ticketon.ticketon.dto.EnqueuedUser;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.ticket.utils.RedisKeyConstants.WAITING_LINE;

@Component
public class WaitingLineBatchWriter {

    private final RedisAsyncCommands<String, String> asyncCommands;
    private final Sinks.Many<EnqueuedUser> sink;
    private final AtomicLong sequence = new AtomicLong();

    private static final String TTL_KEY_PREFIX = "ttl:waiting:";
    private static final long TTL_SECONDS = 60 * 20;

    public WaitingLineBatchWriter(StatefulRedisConnection<String, String> connection) {
        this.asyncCommands = connection.async();
        this.asyncCommands.setAutoFlushCommands(false);
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
        startFlushLoop();
    }

    public void enqueue(String email) {
        long timestamp = System.currentTimeMillis();
        long offset = sequence.getAndIncrement();
        double score = timestamp + (offset * 0.0001);
        sink.tryEmitNext(new EnqueuedUser(email, score));
    }

    private void startFlushLoop() {
        sink.asFlux()
                .bufferTimeout(5000, Duration.ofMillis(20))
                .flatMap(this::writeBatchToRedis)
                .onErrorContinue((e, o) -> {throw new WaitingLineRedisFlushException(e.getMessage());})
                .subscribe();
    }

    private Mono<Void> writeBatchToRedis(List<EnqueuedUser> users) {
        return Mono.fromCallable(() -> {
            List<io.lettuce.core.RedisFuture<?>> futures = new ArrayList<>();
            for (EnqueuedUser user : users) {
                futures.add(asyncCommands.zadd(WAITING_LINE, user.getScore(), user.getEmail()));
                futures.add(asyncCommands.setex(TTL_KEY_PREFIX + user.getEmail(), TTL_SECONDS, "1"));
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
