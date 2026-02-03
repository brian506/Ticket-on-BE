package com.ticketon.ticketon.config;

import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;


import java.util.HashMap;
import java.util.Map;

public abstract class KafkaConsumerConfig {

    protected Map<String,Object> commonConsumerProps(String bootstrapServers,String groupId){
        Map<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
        return props;
    }

    protected <T> ConsumerFactory<String, T> consumerFactory(Map<String,Object> props,Class<T> valueClass){
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(valueClass);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);
        return new DefaultKafkaConsumerFactory<>(props,new StringDeserializer(),deserializer);
        }

    protected <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(
            ConsumerFactory<String,T> consumerFactory,
            int concurrency,
            ContainerProperties.AckMode ackMode,
            boolean batchListener,
            CommonErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(concurrency);
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ackMode);
        factory.setBatchListener(batchListener);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    }

