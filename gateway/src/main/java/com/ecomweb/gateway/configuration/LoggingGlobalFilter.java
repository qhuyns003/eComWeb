package com.ecomweb.gateway.configuration;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public reactor.core.publisher.Mono<Void> filter(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        System.out.println("=== Gateway Forward Request ===");
        System.out.println("Path: " + exchange.getRequest().getPath());
        exchange.getRequest().getHeaders().forEach((k, v) ->
                System.out.println(k + " : " + v));
        System.out.println("===============================");

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    System.out.println("=== Gateway Response ===");
                    exchange.getResponse().getHeaders()
                            .forEach((k, v) -> System.out.println(k + " : " + v));
                    System.out.println("========================");
                }));
    }

    @Override
    public int getOrder() {
        return -1; // chạy sớm
    }
}
