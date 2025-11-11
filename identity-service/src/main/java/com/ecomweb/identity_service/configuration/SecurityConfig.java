package com.ecomweb.identity_service.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

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

    // khong duoc tao ben cho jwtdecode k thi oath2 se lay cai custom cua minh dua ch keycloak xu li -> bi sai
    // oauth cung tu them converter mac dinh

//    @Autowired
//    private CustomJwtDecoder customJwtDecoder;

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

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                            // Nếu có converter, decoder riêng thì thêm ở đây, ví dụ:
                            // jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
                        }
                        )
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }


//    @Bean
//    JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//
//        return jwtAuthenticationConverter;
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


}

// cors là cơ chế mà trình duyệt bảo vệ người dùng khỏi việc 1 domain khác sử dugnj lại cookie của 1 user hợp lệ đã login và lưu trên trình duyệt
// server BE vẫn sẽ thực thi yêu cầu bình thường
// BE chỉ set header để thông báo cho trình duyệt biết domain đó có được phép đọc respose hay không thôi

// csrf mới là cơ chế lợi dụng cookie ng dùng để truy cập vào BE
// -> Spring có cơ chế mặc định tạo 1 token trả về trình duyệt và lưu vào cookie
// domain hợp lệ mới lấy đc token và set header mới gửi đc yêu cầu, hacker k thể lấy đc token này do cơ chế SOP ngăn đọc cookie
// httponly là cơ chế bảo mật của cookie khiến cho chính domain đó cũng k thể đọc đc cookie

// localStorage và cookie đều để lưu trữ dữ liệu
// localStorage lưu các dữ liệu ít nhạy cảm hơn, lớn, k có cơ chế bảo vệ, khôgn tự đôgnj gửi, người dùng phải set header
// cookie thường lưu các dữ liệu nhạy cảm, và có cơ chế bảo mật để tránh bị đọc trộm, tự đông gửi với domain BE hợp lệ
// token jwt lưu trong localStorage nên k cần cơ chế chống csrf
// localStorage chỉ được đọc bởi cùng 1 domain, domain khác khôgn thể đọc được do cơ chế SOP(same origin policy) bảo vệ
// cors cũng quyết định việc domain FE đó có được đọc cookie hay không