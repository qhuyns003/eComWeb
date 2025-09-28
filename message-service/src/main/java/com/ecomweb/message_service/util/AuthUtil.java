package com.ecomweb.message_service.util;


import com.ecomweb.message_service.exception.AppException;
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
