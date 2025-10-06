package com.ecomweb.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.util.List;

// gateway là nơi nhận nhiều rq, cần chịu tải lớn
// k dùng mvc vì chiếm nhiều thread dễ gây tắc nghẽn -> dùng webflux tối ưu
// mvc(tomcat.jetty web server) và webflux(netty web server) là 2 mô hình framework cho ltrinh web
@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomJwtDecoder customJwtDecoder;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.customJwtDecoder = customJwtDecoder;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    // webflux yeu cau config tra ra mono haic flux , k duco tra ra object blocking truc tiep
    // filter danh cho http request (rest)
    // socket ban chat dung http de ket noi truoc xong moi upgrade len socket sau
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
    // bat cors o gateway, tat o service doi voi cac restapi thuong
    // cac api handshake, bypass cors ở filter để thuc hien set cors o websocket tại service -> bắt buộc set cors tại websocketconfig k sẽ bị 403
    // truoc khi thuc hien cors, tat ca api deu gui 1 prefight request (OPTIONS METHOD) de kiem tra truoc
    // preflight duoc config de test phía nhận vào (gateway ở microservice) nếu header đc set ngay thì nó sẽ return luôn k forrward tiếp xuống service
    // api preflight giong het api rest chi khac method
    // neu return null cho 1 api tai filter thi no duoc phep forward xuong service tiep
    // preflight cung se bi forward xuong service thưc hiện tất cả nghiệp vụ , nhân response body, header xong mới về
    // mônlith có để set có cors filter và websocketcorf do mvc có thể nhận diện được handsake socket và bỏ qua cors tại filter, chỉ dùng tại socketconffig (webflux gateway k làm đc), mặt khác nó sẽ ghi đè nếu trùng header vì thuộc cùng 1 server
    // method option la de no k map voi controller nao -> response header nhanh
                .cors(cors -> cors
                        .configurationSource(request -> {
                            String path = request.getRequest().getURI().getPath();
                            // KHÔNG filter CORS cho WebSocket handshake
                            if (path.startsWith("/ws")) {
                                return null;
                            }
                            if (path.startsWith("/notifications/ws-notification")) {
                                return null;
                            }
                            if (path.startsWith("/messages/ws")) {
                                return null;
                            }
                            CorsConfiguration config = new CorsConfiguration();
                            config.addAllowedOrigin("http://localhost:5173");
                            config.addAllowedMethod("*");
                            config.addAllowedHeader("*");
                            config.setAllowCredentials(true);
                            return config;
                        })
                )
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() // permit cors preflight (api truoc khi rq that)
                        .pathMatchers(HttpMethod.GET, API_URL.URL_ANONYMOUS_GET).permitAll()
                        .pathMatchers(HttpMethod.POST, API_URL.URL_ANONYMOUS_POST).permitAll()
                        .pathMatchers(HttpMethod.PUT, API_URL.URL_ANONYMOUS_PUT).permitAll()
                        .pathMatchers(HttpMethod.DELETE, API_URL.URL_ANONYMOUS_DELETE).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                .build();
    }

    @Bean
    ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtGrantedAuthoritiesConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtGrantedAuthoritiesConverter));
        return converter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }



}
