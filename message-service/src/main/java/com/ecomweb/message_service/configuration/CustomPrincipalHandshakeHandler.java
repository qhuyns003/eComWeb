package com.ecomweb.message_service.configuration;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomPrincipalHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Lấy principal đã gán trong AuthHandshakeInterceptor
        Object principal = attributes.get("principal");
        if (principal instanceof Principal) {
            return (Principal) principal;
        }
        // fallback: lấy username nếu có
        Object username = attributes.get("username");
        if (username instanceof String) {
            return () -> (String) username;
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}
