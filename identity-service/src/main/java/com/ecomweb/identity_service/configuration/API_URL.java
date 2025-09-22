package com.ecomweb.identity_service.configuration;

public class API_URL {
    // co context path tu dong them vao, phai can than
    public static final String[] URL_ANONYMOUS_GET = {
            // /* la 1 duong dan /** la nhieu duong dan
            "/actuator/**",
           "/**"

    };

    public static final String[] URL_ANONYMOUS_PUT = {
            // /* la 1 duong dan /** la nhieu duong dan
           "/**"

    };

    // Các API POST cho người dùng không đăng nhập (public)
    public static final String[] URL_ANONYMOUS_POST = {

          "/**"


    };
    public static final String[] URL_ANONYMOUS_DELETE = {

           "/**"

    };
}
