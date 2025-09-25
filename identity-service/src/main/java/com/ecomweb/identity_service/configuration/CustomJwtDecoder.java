package com.ecomweb.identity_service.configuration;


import com.ecomweb.identity_service.dto.request.IntrospectRequest;
import com.ecomweb.identity_service.dto.response.ApiResponse;
import com.ecomweb.identity_service.dto.response.IntrospectResponse;
import com.ecomweb.identity_service.feignClient.MainFeignClient;
import com.ecomweb.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;


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

    AuthenticationService authenticationService;


    @NonFinal
    private NimbusJwtDecoder nimbusJwtDecoder = null;


    @Override
    public Jwt decode(String token) throws JwtException {

    // dung webclient cua webflux ma .block thi toc do cung ngang feign

        IntrospectResponse response = null;
        try {
            response = authenticationService.introspect(IntrospectRequest.builder()
                    .token(token).build());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        if (!response.isValid()) throw new JwtException("Token invalid");

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);

    }
}
