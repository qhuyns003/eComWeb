package com.ecomweb.notification_service.configuration;

public class API_URL {
    public static final String[] URL_ANONYMOUS_GET = {
            // /* la 1 duong dan /** la nhieu duong dan
            "/categories/**",
            "/products/**",
            "/customer_reviews/**",
            "/uploads/**",
            "/user_address/**",
            "/ghn/**",
            "/shop_address/**",
            "/coupons/**",
            "/vnpay/**",
            "/payments/**",
            "/orders/**",
            "/carts/**",
            "/shop/**",
            "/weaviate/**",
            "/notifications/**",
            "/ws-notification/**",
            "/apollo/**",
            "/users/**",
            "/messages/**",
            "/private_chats/**",
            "/rooms/**",
            "/user_rooms/**",

    };

    public static final String[] URL_ANONYMOUS_PUT = {
            // /* la 1 duong dan /** la nhieu duong dan
            "/categories/**",
            "/products/**",
            "/customer_reviews/**",
            "/orders/**",
            "/users/**",
            "/user_address/**",
            "/shop/**",
            "/weaviate/**",
            "/notifications/**",
            "/ws-notification/**",
            "/messages/**",
            "/private_chats/**",
            "/rooms/**",
            "/user_rooms/**",

    };

    // Các API POST cho người dùng không đăng nhập (public)
    public static final String[] URL_ANONYMOUS_POST = {

            "/auth/token",
            "/auth/introspect",
            "/auth/logout",
            "/auth/refresh",
            "/files/**",
            "/products/**",
            "/user_address/**",
            "/ghn/**",
            "/shop_address/**",
            "/vnpay/**",
            "/orders/**",
            "/shop/**",
            "/weaviate/**",
            "/notifications/**",
            "/ws-notification/**",
            "/users/**",
            "/messages/**",
            "/private_chats/**",
            "/rooms/**",
            "/user_rooms/**",


    };
    public static final String[] URL_ANONYMOUS_DELETE = {

            "/files/**",
            "/products/**",
            "/orders/**",
            "/user_address/**",
            "/shop/**",
            "/weaviate/**",
            "/notifications/**",
            "/ws-notification/**",
            "/users/**",
            "/messages/**",
            "/private_chats/**",
            "/rooms/**",
            "/user_rooms/**",

    };
}
