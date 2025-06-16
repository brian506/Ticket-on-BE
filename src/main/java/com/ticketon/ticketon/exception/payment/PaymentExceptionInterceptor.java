package com.ticketon.ticketon.exception.payment;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

// 예외 처리 interceptor
public class PaymentExceptionInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try{
            return execution.execute(request, body);
        }catch (IOException e){
            throw new RuntimeException(e);
        }catch(Exception e){
            throw new IOException(e);
        }
    }
}
