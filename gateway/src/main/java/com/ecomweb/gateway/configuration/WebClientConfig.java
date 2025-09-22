package com.ecomweb.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    @Bean("identityService")
    public WebClient mainWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084/identity")
                .build();
    }
}

