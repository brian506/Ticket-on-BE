package com.ticketon.ticketon.producer;

import com.ticketon.ticketon.consumer.WaitingLineBatchWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WaitingLineProducer {


    private final KafkaSender<String, String> sender;
    private final String topic;

    public WaitingLineProducer(@Value("${kafka.producer.bootstrap-servers}") String bootstrapServers,
                                      @Value("${kafka.topic-config.queue-enqueue.name}") String topic) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        this.topic = topic;

        SenderOptions<String, String> senderOptions = SenderOptions.create(props);
        this.sender = KafkaSender.create(senderOptions);
    }

    public Mono<Void> send(String email) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, email);
        SenderRecord<String, String, String> record = SenderRecord.create(producerRecord, email);
        return sender.send(Mono.just(record))
                .doOnError(e -> log.error("Kafka 전송 실패", e))
                .then();
    }

//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Value("${kafka.topic-config.queue-enqueue.name}")
//    private String topic;
//
//    public WaitingLineProducer(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendQueueEnterMessage(final String email) {
//        kafkaTemplate.send(topic, email);
//    }
}
