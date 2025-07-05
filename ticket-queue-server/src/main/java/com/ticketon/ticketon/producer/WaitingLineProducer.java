package com.ticketon.ticketon.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WaitingLineProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic-config.queue-enqueue.name}")
    private String topic;

    public WaitingLineProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendQueueEnterMessage(final String email) {
        kafkaTemplate.send(topic, email);
    }
}
