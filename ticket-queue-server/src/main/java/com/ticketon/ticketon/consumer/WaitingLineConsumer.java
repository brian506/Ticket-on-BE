package com.ticketon.ticketon.consumer;

import com.ticket.exception.custom.KafkaConsumerException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

@Component
public class WaitingLineConsumer {

    public WaitingLineConsumer(WaitingLineBatchWriter batchWriter,
                               @Value("${kafka.consumer.queue-enqueue.bootstrap-servers}") String bootstrapServers,
                               @Value("${kafka.topic-config.queue-enqueue.name}") String topic,
                               @Value("${kafka.consumer.queue-enqueue.group-id}") String groupId) {
        Map<String, Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );

        ReceiverOptions<String, String> options = ReceiverOptions.<String, String>create(props)
                .subscription(List.of(topic));

        KafkaReceiver.create(options)
                .receive()
                .doOnNext(record -> {
                    batchWriter.enqueue(record.value());
                })
                .doOnError(e -> {
                    throw new KafkaConsumerException(e.getMessage());
                })
                .subscribe();
    }
}
