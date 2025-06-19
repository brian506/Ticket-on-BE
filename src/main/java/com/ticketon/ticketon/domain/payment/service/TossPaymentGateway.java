package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.config.PaymentProperties;
import com.ticketon.ticketon.exception.custom.PaymentCancelException;
import com.ticketon.ticketon.exception.custom.PaymentConfirmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentGateway implements PaymentGateway {

    private final PaymentProperties paymentProperties;
    private final RestClient restClient;

    //todo 예외 던져줄때 토스에서 전달받은 에러 메시지가 반드시 필요할 경우 예외 메시지만 추출하는 로직 추가
    @Override
    public PaymentConfirmResponse requestPaymentConfirm(PaymentConfirmRequest request) {
        TossConfirmRequest tossRequest = request.toTossConfirmRequest();
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getConfirmUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(tossRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new PaymentConfirmException("결제 승인 요청 실패: Toss 응답 오류");
                })
                .body(PaymentConfirmResponse.class);
    }

    @Override
    public PaymentCancelResponse requestPaymentCancel(PaymentCancelRequest request) {
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getCancelUrl(request.getPaymentKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new PaymentCancelException("결제 취소 요청 실패: Toss 응답 오류");
                })
                .body(PaymentCancelResponse.class);
    }
}