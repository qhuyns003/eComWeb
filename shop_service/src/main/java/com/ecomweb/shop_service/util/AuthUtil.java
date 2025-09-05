package com.ecomweb.shop_service.util;


import com.ecomweb.shop_service.exception.AppException;
import com.ecomweb.shop_service.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
