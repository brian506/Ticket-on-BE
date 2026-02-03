package com.ticketon.ticketon.domain.ticket.infra;

import com.ticketon.ticketon.domain.ticket.dto.NewTicketEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Configuration
@EnableKafka
@Slf4j
public class TicketConsumerConfig {

    @Value("${kafka.consumer.ticket-group.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.ticket-group.group-id}")
    private String ticketGroupId;

    @Bean("ticketConsumerConfigs")
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ticketGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "20000");
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "5000");

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NewTicketEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES,"*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean("ticketConsumerFactory")
    public ConsumerFactory<String, NewTicketEvent> consumerFactory() {
        JsonDeserializer<NewTicketEvent> deserializer = new JsonDeserializer<>(NewTicketEvent.class);
        deserializer.configure(consumerConfigs(),false);
        ErrorHandlingDeserializer<NewTicketEvent> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(deserializer);
        Map<String, Object> props = consumerConfigs();
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean("ticketKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, NewTicketEvent> ticketContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NewTicketEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(5);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

}
