package com.ecomweb.message_service.configuration;


import com.ecomweb.message_service.dto.request.ApiResponse;
import com.ecomweb.message_service.dto.request.IntrospectRequest;
import com.ecomweb.message_service.dto.response.IntrospectResponse;
import com.ecomweb.message_service.feignClient.IdentityFeignClient;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
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

    IdentityFeignClient identityFeignClient;

    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        ApiResponse<IntrospectResponse> response = identityFeignClient.introspect(IntrospectRequest.builder()
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
