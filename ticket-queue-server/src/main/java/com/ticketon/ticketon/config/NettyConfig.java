package com.ticketon.ticketon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import reactor.netty.resources.LoopResources;

@Configuration
public class NettyConfig {

    @Bean
    public ReactorResourceFactory reactorServerResourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false); // 커스텀 리소스 사용
        factory.setLoopResources(
                LoopResources.create("http-server", 16, true) // Netty event loop thread 설정
        );
        return factory;
    }
}
