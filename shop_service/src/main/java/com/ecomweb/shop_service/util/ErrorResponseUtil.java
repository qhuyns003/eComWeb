package com.ecomweb.shop_service.util;


import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.exception.AppException;
import com.ecomweb.shop_service.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
public class ErrorResponseUtil {

    public static ApiResponse getResponseBody(WebClientResponseException ex) {

        ApiResponse errorBody;
        try {
            errorBody = new ObjectMapper().readValue(ex.getResponseBodyAsString(), ApiResponse.class);
            log.info(ex.getResponseBodyAsString());
            errorBody.setHttpStatus(HttpStatus.valueOf(ex.getRawStatusCode()));
        } catch (Exception e) {
            errorBody = new ApiResponse();
            errorBody.setMessage("Cannot parse error body");
            errorBody.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return errorBody;
    }
}
