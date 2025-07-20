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

    };

    public static final String[] URL_ANONYMOUS_PUT = {
            // /* la 1 duong dan /** la nhieu duong dan
            "/categories/**",
            "/products/**",
            "/customer_reviews/**"

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

    };
    public static final String[] URL_ANONYMOUS_DELETE = {

            "/files/**",
            "/products/**"

    };
}
