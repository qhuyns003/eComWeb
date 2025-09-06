package com.ecomweb.shop_service.util;


import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.exception.AppException;
import com.ecomweb.shop_service.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ErrorResponseUtil {

    public static ApiResponse getResponseBody(WebClientResponseException ex) {

        ApiResponse errorBody;
        try {
            errorBody = new ObjectMapper().readValue(ex.getResponseBodyAsString(), ApiResponse.class);
            errorBody.setHttpStatus(HttpStatus.valueOf(ex.getRawStatusCode()));
        } catch (Exception e) {
            errorBody = new ApiResponse();
            errorBody.setMessage("Cannot parse error body");
        }
        return errorBody;
    }
}
