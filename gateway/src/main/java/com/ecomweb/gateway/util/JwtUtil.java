package com.ecomweb.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private static final String SECRET_KEY = "1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij" ;
//    static {
//        SECRET_KEY = System.getProperty("jwt.signerKey");
//        // hoặc dùng Environment, hoặc ResourceBundle để đọc từ file properties
//        // Hoặc hardcode tạm thời: SECRET_KEY = "your_secret_key";
//    }

    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
        return claims.getSubject(); // subject thường là username
    }
}
