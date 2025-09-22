package com.ecomweb.identity_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    @Bean("mainService")
    public WebClient mainWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/")
                .build();
    }
}

