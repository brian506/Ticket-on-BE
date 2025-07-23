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
        factory.setUseGlobalResources(false);
        factory.setLoopResources(
                LoopResources.create("http-server", 16, true) // Netty 이벤트 루프 thread 설정
        );
        return factory;
    }
}
