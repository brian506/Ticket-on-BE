package com.ticketon.ticketon.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka // kafka 리스너 사용가능하게 해줌
public class KafkaConsumerConfig {

    @Value("${kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;


    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        // 수정 필요
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest.java"); // kafka 메시지를 어떤 클래스로 역직렬화할지
        return props;
    }

    // consumer 인스턴스 만들어주는 객체
    // 여기서의 value(object) 는 결제 요청 dto
    @Bean
    public ConsumerFactory<String,Object> consumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
        }

    // kafkaListener 가 수신할 수 있도록
    // kafkaListener? : 지정한 토픽, 파티션에서 주기적으로 메시지를 poll 함(기본적으로 싱글 스레드)
    // 하나의 파티션을 하나의 consumer 스레드에 할당
    // 파티션 수에 따라서 병렬처리가 늘어남
    // 토픽은 업무 도메인 또는 메시지 종류별로 분리(주문,결제 구분 등)
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(3); // 3개의 컨슈머 스레드로 병렬처리
        factory.setConsumerFactory(consumerFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
    /**
     * ack 설정 방법
     * manual : 커스텀
     * record : 레코드마다 자동 커밋
     * batch : 한 번에 받은 레코드 처리 후 자동 커밋
     * Time,count :
     */

    }

