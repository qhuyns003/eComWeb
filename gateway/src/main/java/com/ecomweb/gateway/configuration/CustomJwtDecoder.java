package com.ecomweb.gateway.configuration;

import com.ecomweb.gateway.dto.request.IntrospectRequest;
import com.ecomweb.gateway.dto.response.ApiResponse;
import com.ecomweb.gateway.dto.response.IntrospectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class CustomJwtDecoder implements ReactiveJwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final WebClient webClient;

    private NimbusReactiveJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {
 // bản thân gateway phải xây trên webflux => tất cả các api phải nên gọi dưới dngj non-blocking api để tận dụng ưu điểm của webflux

        return webClient.post()
                .uri("/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(IntrospectRequest.builder().token(token).build())
                .retrieve()// gui rq
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<IntrospectResponse>>() {}) // parse json ra object
                .flatMap(response -> {  // xu li kq
                    if (!response.getResult().isValid()) {
                        // su dung badjwtexception bat loi chuan hon jwtexception vi jwtexc qua rong
                        return Mono.error(new BadJwtException("Token invalid"));
                    }
                    if (nimbusJwtDecoder == null) {
                        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
                        nimbusJwtDecoder = NimbusReactiveJwtDecoder
                                .withSecretKey(secretKeySpec)
                                .macAlgorithm(MacAlgorithm.HS512)
                                .build();
                    }

                    return nimbusJwtDecoder.decode(token);
                });
    }
}
