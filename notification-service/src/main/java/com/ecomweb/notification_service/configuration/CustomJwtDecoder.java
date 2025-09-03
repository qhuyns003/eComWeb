package com.ecomweb.notification_service.configuration;


import com.ecomweb.notification_service.dto.request.IntrospectRequest;
import com.ecomweb.notification_service.dto.response.ApiResponse;
import com.ecomweb.notification_service.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;


@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Qualifier("mainService")
    private final WebClient webClient;

    private NimbusReactiveJwtDecoder nimbusJwtDecoder = null;

    public CustomJwtDecoder(WebClient webClient) {
        this.webClient = webClient;
    }


    @Override
    public Jwt decode(String token) throws JwtException {


       return
               webClient.post()
                       .uri("/auth/introspect")
                       .contentType(MediaType.APPLICATION_JSON)
                       .bodyValue(IntrospectRequest.builder().token(token).build())
                       .retrieve()// gui rq
                       .bodyToMono(new ParameterizedTypeReference<ApiResponse<IntrospectResponse>>() {}) // parse json ra object
                       .flatMap(response -> {  // xu li kq
                           if (!response.getResult().isValid()) {
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
                       })
                       .block() ;// block ep mono ve data thuong;

    }
}
