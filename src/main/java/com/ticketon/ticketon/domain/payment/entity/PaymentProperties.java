package com.ticketon.ticketon.domain.payment.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toss")
@Getter
@Setter
public class PaymentProperties {

    /**
     * application.yml 에 있는 값을 자동으로 불러와서 객체에 바인딩함
     */

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
