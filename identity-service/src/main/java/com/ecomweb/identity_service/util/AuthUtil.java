package com.ecomweb.identity_service.util;


import com.ecomweb.identity_service.exception.AppException;
import com.ecomweb.identity_service.exception.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthUtil {

    public static String getToken() {


        JwtAuthenticationToken authentication =
                    (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null){
            throw  new AppException(ErrorCode.UNAUTHENTICATED);
        }
         String   token = authentication.getToken().getTokenValue();

        return token;
    }
}
