package com.ticketon.ticketon.domain.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.entity.PaymentProperties;
import com.ticketon.ticketon.exception.payment.PaymentCancelException;
import com.ticketon.ticketon.exception.payment.PaymentConfirmException;
import com.ticketon.ticketon.exception.payment.PaymentExceptionInterceptor;
import com.ticketon.ticketon.exception.payment.PaymentResponseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Slf4j
@Component
public class PaymentClient {

    private static final int CONNECTION_TIMEOUT_SECONDS = 1;
    private static final int READ_TIMEOUT_SECONDS = 30;

    private final PaymentProperties paymentProperties;
    private final ObjectMapper objectMapper;
    private RestClient restClient;

    public PaymentClient(PaymentProperties paymentProperties, ObjectMapper objectMapper) {
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .requestInterceptor(new PaymentExceptionInterceptor())
                .defaultHeader(HttpHeaders.AUTHORIZATION,createPaymentAuthHeader(paymentProperties))
                .build();
    }

    // client 와 server 에서의 대기 타임
    @Bean
    public RestClient restClient(){
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECONDS));
        factory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    private String createPaymentAuthHeader (PaymentProperties paymentProperties){
        byte[] encodedBytes = Base64.getEncoder().encode((paymentProperties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest paymentConfirmRequest){
        TossConfirmRequest tossConfirmRequest = new TossConfirmRequest(
                paymentConfirmRequest.getPaymentKey(),
                paymentConfirmRequest.getTicketId(),
                paymentConfirmRequest.getAmount()
        );
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getConfirmUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(tossConfirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> {
                    throw new PaymentConfirmException(String.valueOf(getPaymentErrorCode(response)));
                })
                .body(PaymentConfirmResponse.class);
    }
    public PaymentCancelResponse cancelPayment(PaymentCancelRequest paymentCancelRequest){
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getCancelUrl(paymentCancelRequest.getPaymentKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCancelRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> {
                    throw new PaymentCancelException(String.valueOf(getPaymentErrorCode(response)));
                })
                .body(PaymentCancelResponse.class);
    }

    // 토스에서 응답 받은 에러 코드를 서버에서 볼 수 있도록
    private PaymentResponseErrorCode getPaymentErrorCode(final ClientHttpResponse response) throws IOException {
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.info(" Toss 응답 바디: " + body);
        try {
            PaymentFailResponse failResponse = objectMapper.readValue(body, PaymentFailResponse.class);
            return PaymentResponseErrorCode.findByCode(failResponse.getCode());
        } catch (Exception e) {
            throw new PaymentConfirmException("응답 파싱 실패: " + body);
        }
    }


}

