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

    private String secretKey;
    private String baseUrl;
    private String confirmEndPoint;


}
