package com.ticketon.ticketon.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WaitingLineProducer {

    private final KafkaSender<String, String> sender;
    private final String topic;
    private final Sinks.Many<String> sink;

    public WaitingLineProducer(@Value("${kafka.producer.bootstrap-servers}") String bootstrapServers,
                               @Value("${kafka.topic-config.queue-enqueue.name}") String topic) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, String> senderOptions = SenderOptions.create(props);

        this.topic = topic;
        this.sender = KafkaSender.create(senderOptions);
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
        startKafkaSenderLoop();
    }

    public void enqueue(String email) {
        sink.tryEmitNext(email);
    }

    private void startKafkaSenderLoop() {
        sink.asFlux()
                .bufferTimeout(1000, Duration.ofMillis(10))
                .flatMap(this::sendBatch)
                .onErrorContinue((e, o) -> log.error("Kafka 전송 중 에러", e))
                .subscribe();
    }

    private Mono<Void> sendBatch(List<String> emails) {
        List<SenderRecord<String, String, String>> records = emails.stream()
                .map(email -> {
                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, email);
                    return SenderRecord.create(producerRecord, email);
                })
                .toList();

        return sender.send(Flux.fromIterable(records)).then();
    }
}

