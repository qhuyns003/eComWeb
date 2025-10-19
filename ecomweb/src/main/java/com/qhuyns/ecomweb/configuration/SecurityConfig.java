package com.qhuyns.ecomweb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
        "/users", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };
    // Qualifier chi dinh class nao duoc ịnject khi DI bang interface (trg hop nhieu class impl interface)
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
        // hasRole("ADMIN") -> role :ROLE_ADMIN
        // hasAuthority("ADMIN") -> role : ADMIN

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder) // decode token ra
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())) // set authentication
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())); // xu li khi bi co loi xac thuc
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }



    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        // tim claim ten la scope va chuyen chuoi thanh list vd "1 2" -> [1,2]
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // set prefix cho moi phan tu trong list vua duoc chuyen tu scope -> gan vao authorities
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        // sub tu dong duoc mapping vao pricipal name

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
//    @Bean
//    public RoleHierarchy roleHierarchy() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        String hierarchy = "ROLE_ADMIN > ROLE_USER  ROLE_ADMIN > ROLE_SELLER";
//        roleHierarchy.setHierarchy(hierarchy);
//        return roleHierarchy;
//    }

}
