package com.ticketon.ticketon.exception;

import com.ticketon.ticketon.exception.custom.NotFoundDataException;
import com.ticketon.ticketon.exception.payment.PaymentConfirmException;
import com.ticketon.ticketon.exception.payment.PaymentResponseErrorCode;
import com.ticketon.ticketon.utils.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class ExceptionResolver {

    private static final Logger logger = LogManager.getLogger(ExceptionResolver.class);

    @ExceptionHandler({NotFoundDataException.class})
    @ResponseBody
    public ResponseEntity<SuccessResponse> handleClientException(HttpServletRequest request, Exception exception) {
        ExceptionBase ex = (ExceptionBase) exception;
        logException(request, ex);
        SuccessResponse response = new SuccessResponse(false, ex.getErrorMessage(), ex.getErrorCode().getCode());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(PaymentConfirmException.class)
    @ResponseBody
    public ResponseEntity<SuccessResponse> handlePaymentConfirmException(HttpServletRequest request, PaymentConfirmException exception) {
        logger.error("PaymentConfirmException occurred: {}", exception.getMessage(), exception);

        SuccessResponse response = new SuccessResponse(false,"결제 확인 중 오류가 발생했습니다", PaymentResponseErrorCode.UNKNOWN_PAYMENT_ERROR);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private void logException(HttpServletRequest request, ExceptionBase exception) {
        String requestBody = extractRequestBody(request);
        logger.error("Request URL: {}, Method: {}, Exception: {}, Request Body: {}",
                request.getRequestURL(),
                request.getMethod(),
                exception.getErrorMessage(),
                requestBody);
    }

    private String extractRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return "N/A";
    }
}
