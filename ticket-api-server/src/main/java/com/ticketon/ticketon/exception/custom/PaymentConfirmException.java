package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public class PaymentConfirmException extends RuntimeException {

    private HttpStatusCode code;
    private String errorMessage;



//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.INTERNAL_SERVER_ERROR;
//    }
}
