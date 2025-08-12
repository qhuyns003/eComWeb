package com.qhuyns.ecomweb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
// websocket giup client thay du lieu realtime ngay khong can reload nhu http
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    /topic/: gửi cho nhiều người
///queue/: gửi cho từng người
///app: prefix cho client gửi lên server
///user: prefix cho channel riêng từng user
///ws: endpoint để client connect

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/", "/queue/");
        config.setApplicationDestinationPrefixes("/ws-app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
