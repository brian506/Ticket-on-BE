package com.ticketon.ticketon.config;

import com.ticketon.ticketon.domain.queue.handler.QueueWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final QueueWebSocketHandler queueWebSocketHandler;

    public WebSocketConfig(QueueWebSocketHandler queueWebSocketHandler) {
        this.queueWebSocketHandler = queueWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(queueWebSocketHandler, "/queue-ws")
                .setAllowedOrigins("*");
    }
}