package com.ticketon.ticketon.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.ticketon.ticketon.utils.RedisUtils.stripQuotesAndTrim;


@Component
public class WaitingLineConsumer {

    private final WaitingLineBatchWriter batchWriter;

    public WaitingLineConsumer(WaitingLineBatchWriter batchWriter) {
        this.batchWriter = batchWriter;
    }

    @KafkaListener(
            topics = "${kafka.topic-config.queue-enqueue.name}",
            groupId = "${kafka.consumer.queue-enqueue.group-id}",
            containerFactory = "waitingEnqueueKafkaListenerContainerFactory"
    )
    public void listen(final String message) {
        String email = stripQuotesAndTrim(message);
        batchWriter.enqueue(email); // Redis 대신 버퍼에 위임
    }
}
