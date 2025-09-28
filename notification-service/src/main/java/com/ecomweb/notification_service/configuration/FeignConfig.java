package com.ecomweb.notification_service.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

// webclient toi uu cho cac api bat dong bo (khong van ket qua ngay)
// neu block tren wwebclient thi hieu suat tuong tu resttemplate va feign
@Configuration
public class FeignConfig {

    // config token cho cac feign request
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                String token = jwtAuth.getToken().getTokenValue();
                template.header("Authorization", "Bearer " + token);
            } else {
                // Nếu là AnonymousAuthenticationToken thì bỏ qua, không set header
            }
        };
    }
}
