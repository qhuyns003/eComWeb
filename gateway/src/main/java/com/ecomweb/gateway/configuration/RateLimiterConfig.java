package com.ecomweb.gateway.configuration;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
// sinh key cho redis, dua tren ip ng gui
public class RateLimiterConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange ->
                Mono.just(exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress());
    }


}

