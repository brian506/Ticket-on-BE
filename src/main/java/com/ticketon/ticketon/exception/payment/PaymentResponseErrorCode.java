package com.ticketon.ticketon.exception.payment;

import com.ticketon.ticketon.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PaymentResponseErrorCode implements ErrorCode {
    // 결제 승인
    ALREADY_PROCESSED_PAYMENT(HttpStatus.BAD_REQUEST, "이미 처리된 결제 입니다.",400),
    PROVIDER_ERROR(HttpStatus.BAD_REQUEST, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",400),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.",400),
    INVALID_API_KEY(HttpStatus.BAD_REQUEST, "잘못된 시크릿키 연동 정보 입니다.",400),
    INVALID_EXCEED_MAX_DAILY_PAYMENT_COUNT(HttpStatus.BAD_REQUEST, "하루 결제 가능 횟수를 초과했습니다.",400),
    EXCEED_MAX_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "하루 결제 가능 금액을 초과했습니다.",400),
    NOT_FOUND_TERMINAL_ID(HttpStatus.BAD_REQUEST, "단말기번호(Terminal Id)가 없습니다. 관리자에게 문의해주세요.",400),
    UNAPPROVED_ORDER_ID(HttpStatus.BAD_REQUEST, "아직 승인되지 않은 주문번호입니다.",400),
    UNAUTHORIZED_KEY(HttpStatus.BAD_REQUEST, "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.",400),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "결제 비밀번호가 일치하지 않습니다.",400),
    NOT_FOUND_PAYMENT(HttpStatus.BAD_REQUEST, "존재하지 않는 결제 정보 입니다.",400),
    NOT_FOUND_PAYMENT_SESSION(HttpStatus.BAD_REQUEST, "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.",400),
    INVALID_AUTHORIZE_AUTH(HttpStatus.BAD_GATEWAY, "유효하지 않은 인증 방식입니다.",502),
    NOT_AVAILABLE_PAYMENT(HttpStatus.BAD_GATEWAY, "결제가 불가능한 시간대입니다",502),
    INCORRECT_BASIC_AUTH_FORMAT(HttpStatus.BAD_GATEWAY, "서버 측 오류로 결제에 실패했습니다. 관리자에게 문의해주세요.",502),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(HttpStatus.BAD_GATEWAY, "결제가 완료되지 않았어요. 다시 시도해주세요.",502),
    FAILED_INTERNAL_SYSTEM_PROCESSING(HttpStatus.BAD_GATEWAY, "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.",502),
    UNKNOWN_PAYMENT_ERROR(HttpStatus.BAD_GATEWAY, "결제에 실패했어요. 같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.",502),

    // 결제 취소
    ALREADY_CANCELED_PAYMENT(HttpStatus.BAD_REQUEST, "이미 취소된 결제 입니다.",400),
    INVALID_REFUND_ACCOUNT_INFO(HttpStatus.BAD_REQUEST, "환불 계좌번호와 예금주명이 일치하지 않습니다.",400),
    EXCEED_CANCEL_AMOUNT_DISCOUNT_AMOUNT(HttpStatus.BAD_REQUEST, "즉시할인금액보다 적은 금액은 부분취소가 불가능합니다.",400),
    INVALID_REFUND_ACCOUNT_NUMBER(HttpStatus.BAD_REQUEST, "잘못된 환불 계좌번호입니다.",400),
    INVALID_BANK(HttpStatus.BAD_REQUEST, "유효하지 않은 은행입니다.",400),
    NOT_MATCHES_REFUNDABLE_AMOUNT(HttpStatus.BAD_REQUEST, "잔액 결과가 일치하지 않습니다.",400),
    REFUND_REJECTED(HttpStatus.BAD_REQUEST, "환불이 거절됐습니다. 결제사에 문의 부탁드립니다.",400),
    ALREADY_REFUND_PAYMENT(HttpStatus.BAD_REQUEST, "이미 환불된 결제입니다.",400),
    FORBIDDEN_BANK_REFUND_REQUEST(HttpStatus.BAD_REQUEST, "고객 계좌가 입금이 되지 않는 상태입니다.",400),
    NOT_CANCELABLE_AMOUNT(HttpStatus.FORBIDDEN, "취소 할 수 없는 금액 입니다.",403),
    FORBIDDEN_CONSECUTIVE_REQUEST(HttpStatus.FORBIDDEN, "반복적인 요청은 허용되지 않습니다. 잠시 후 다시 시도해주세요.",403),
    FORBIDDEN_REQUEST(HttpStatus.FORBIDDEN, "허용되지 않은 요청입니다.",403),
    NOT_CANCELABLE_PAYMENT(HttpStatus.FORBIDDEN, "취소 할 수 없는 결제 입니다.",403),
    EXCEED_MAX_REFUND_DUE(HttpStatus.FORBIDDEN, "환불 가능한 기간이 지났습니다.",403),
    NOT_ALLOWED_PARTIAL_REFUND_WAITING_DEPOSIT(HttpStatus.FORBIDDEN, "입금 대기중인 결제는 부분 환불이 불가합니다.",403),
    NOT_ALLOWED_PARTIAL_REFUND(HttpStatus.FORBIDDEN, "에스크로 주문, 현금 카드 결제일 때는 부분 환불이 불가합니다. 이외 다른 결제 수단에서 부분 취소가 되지 않을 때는 토스페이먼츠에 문의해 주세요.",403),
    NOT_AVAILABLE_BANK(HttpStatus.FORBIDDEN, "은행 서비스 시간이 아닙니다.",403),
    NOT_CANCELABLE_PAYMENT_FOR_DORMANT_USER(HttpStatus.FORBIDDEN, "휴면 처리된 회원의 결제는 취소할 수 없습니다.",403),
    FAILED_REFUND_PROCESS(HttpStatus.INTERNAL_SERVER_ERROR, "은행 응답시간 지연이나 일시적인 오류로 환불요청에 실패했습니다.",500),
    FAILED_METHOD_HANDLING_CANCEL(HttpStatus.INTERNAL_SERVER_ERROR, "취소 중 결제 시 사용한 결제 수단 처리과정에서 일시적인 오류가 발생했습니다.",500),
    FAILED_PARTIAL_REFUND(HttpStatus.INTERNAL_SERVER_ERROR, "은행 점검, 해약 계좌 등의 사유로 부분 환불이 실패했습니다.",500),
    COMMON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",500),
    FAILED_CLOSED_ACCOUNT_REFUND(HttpStatus.INTERNAL_SERVER_ERROR, "해약된 계좌로 인해 환불이 실패했습니다.",500),

    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류입니다.",500); // 예외 대응용 기본값

    private final HttpStatus httpStatus;
    private final String message;
    private final int code;


    public static PaymentResponseErrorCode findByCode(String tossCode) {
        return Arrays.stream(values())
                .filter(v -> v.name().equals(tossCode))
                .findAny()
                .orElse(UNKNOWN_PAYMENT_ERROR);
    }


}
