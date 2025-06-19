package com.ticketon.ticketon.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toss")
@Getter
@Setter
public class PaymentProperties {

    private String secretKey;

    private String baseUrl;

    private String confirmEndPoint;

    private String cancelEndPoint;

    public String getConfirmUrl(){
        return baseUrl + confirmEndPoint;
    }

    public String getCancelUrl(String paymentKey){
        return String.format(baseUrl + cancelEndPoint,paymentKey);
    }
}
