package com.ecomweb.shop_service.configuration;


import com.ecomweb.shop_service.dto.request.IntrospectRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.IntrospectResponse;
import com.ecomweb.shop_service.feignClient.MainFeignClient;
import com.ecomweb.shop_service.util.ErrorResponseUtil;
import com.nimbusds.jose.JOSEException;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    @NonFinal
    String signerKey;

    MainFeignClient  mainFeignClient;

    @NonFinal
    private NimbusJwtDecoder nimbusJwtDecoder = null;


    @Override
    public Jwt decode(String token) throws JwtException {

    // dung webclient cua webflux ma .block thi toc do cung ngang feign

            ApiResponse<IntrospectResponse> response = mainFeignClient.introspect(IntrospectRequest.builder()
                    .token(token).build());


        if (!response.getResult().isValid()) throw new JwtException("Token invalid");

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);

    }
}
