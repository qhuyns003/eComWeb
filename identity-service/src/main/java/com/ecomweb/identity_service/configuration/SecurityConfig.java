package com.ecomweb.identity_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
// config cua rest
// phan UserDetail chi su dung trong monolith service do chi chay 1 app duy nhat, sessionId luu tren trinh duyet se khop sesionId trong server, se lay lai duoc du lieu
// k sd trong microservice vi khi xac thuc thi session + thongtin UserDetail chi xuat hien trong identity service -> neu rq sau goi sang service khac se k mapping dung
// UserDetails cua springsecu, khi login se luu thong tin UserDeatil + sessionId trong 1 hash

public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
        "/users", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // luôn decode kiểm tra token trước khi permit
                // ứng với mỗi request, sẽ có context khác nhau tùy vào token gửi theo
                .authorizeHttpRequests(request -> request // nếu không yêu cầu token mà vẫn nhét token vào sẽ bị unauthorize
                        .requestMatchers(HttpMethod.GET, API_URL.URL_ANONYMOUS_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, API_URL.URL_ANONYMOUS_POST).permitAll()
                        .requestMatchers(HttpMethod.PUT, API_URL.URL_ANONYMOUS_PUT).permitAll()
                        .requestMatchers(HttpMethod.DELETE, API_URL.URL_ANONYMOUS_DELETE).permitAll()
                .anyRequest()
                .authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


}
