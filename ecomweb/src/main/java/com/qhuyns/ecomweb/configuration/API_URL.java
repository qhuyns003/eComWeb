package com.qhuyns.ecomweb.configuration;

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
            "/ws/**",
            "/apollo/**"

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
            "/ws/**"

    };

    // Các API POST cho người dùng không đăng nhập (public)
    public static final String[] URL_ANONYMOUS_POST = {
            "/users",
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
            "/ws/**"


    };
    public static final String[] URL_ANONYMOUS_DELETE = {

            "/files/**",
            "/products/**",
            "/orders/**",
            "/user_address/**",
            "/shop/**",
            "/weaviate/**",
            "/notifications/**",
            "/ws/**"

    };
}
