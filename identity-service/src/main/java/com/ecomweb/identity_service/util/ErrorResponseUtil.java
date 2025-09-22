package com.ecomweb.identity_service.util;


import com.ecomweb.identity_service.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
// feign error
public class ErrorResponseUtil {

    public static ApiResponse getResponseBody(FeignException ex) {

        ApiResponse errorBody;
        try {
            errorBody = new ObjectMapper().readValue(ex.contentUTF8(), ApiResponse.class);
            errorBody.setHttpStatus(HttpStatus.valueOf(ex.status()));
        } catch (Exception e) {
            errorBody = new ApiResponse();
            errorBody.setMessage("Cannot parse error body");
            errorBody.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return errorBody;
    }
}
