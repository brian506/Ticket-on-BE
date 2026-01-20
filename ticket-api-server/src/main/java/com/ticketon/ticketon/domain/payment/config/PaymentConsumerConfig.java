package com.ticketon.ticketon.domain.payment.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.security.oauthbearer.internals.secured.ValidateException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Configuration
@EnableKafka
@Slf4j
public class PaymentConsumerConfig {

    @Value("${kafka.consumer.payment-group.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.payment-group.group-id}")
    private String paymentGroupId;


    @Bean("paymentConsumerConfigs")
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, paymentGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,100); // 한번에 가져올 최대 메시지 수
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        return props;
    }

    @Bean("paymentConsumerFactory")
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = consumerConfigs();
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("paymentKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(3); // 3개의 컨슈머 스레드로 병렬처리
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> new TopicPartition(r.topic() + ".DLT", r.partition()));

        FixedBackOff backOff = new FixedBackOff(3000L, 3L);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        handler.addNotRetryableExceptions(DeserializationException.class, MethodArgumentNotValidException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (deliveryAttempt > 3) {
                log.error("[Kafka 최종 실패 알림] 메시지 처리에 최종 실패하여 DLT로 전송합니다. " +
                        "Payload: {}, Reason: {}", record.value(), ex.getMessage());
            }
        });
        return handler;
    }

//    @Bean("paymentKafkaBatchListenerContainerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String,PaymentMessage> batchKafkaListenerContainerFactory(){
//        ConcurrentKafkaListenerContainerFactory<String,PaymentMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setConcurrency(3);
//        factory.setBatchListener(true);
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//        return factory;
//    }
}

