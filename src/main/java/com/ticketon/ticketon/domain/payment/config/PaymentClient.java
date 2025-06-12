package com.ticketon.ticketon.domain.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.entity.PaymentProperties;
import com.ticketon.ticketon.exception.payment.PaymentCancelException;
import com.ticketon.ticketon.exception.payment.PaymentConfirmException;
import com.ticketon.ticketon.exception.payment.PaymentExceptionInterceptor;
import com.ticketon.ticketon.exception.payment.PaymentResponseErrorCode;
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
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getBaseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> {
                    throw new PaymentConfirmException(String.valueOf(getPaymentConfirmErrorCode(response)));
                })
                .body(PaymentConfirmResponse.class);
    }
    public PaymentCancelResponse cancelResponse(PaymentCancelRequest paymentCancelRequest){
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getCancelUrl(paymentCancelRequest.getPaymentKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCancelRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> {
                    throw new PaymentCancelException(String.valueOf(getPaymentConfirmErrorCode(response)));
                })
                .body(PaymentCancelResponse.class);
    }

    // 토스에서 응답 받은 에러 코드를 서버에서 볼 수 있도록
    private PaymentResponseErrorCode getPaymentConfirmErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentFailResponse confirmFailResponse = objectMapper.readValue(
                response.getBody(),PaymentFailResponse.class);
        return PaymentResponseErrorCode.findByCode(confirmFailResponse.getCode());
    }

    private PaymentResponseErrorCode getPaymentCancelErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentFailResponse paymentFailResponse = objectMapper.readValue(
                response.getBody(),PaymentFailResponse.class);
        return PaymentResponseErrorCode.findByCode(paymentFailResponse.getCode());
    }

}

