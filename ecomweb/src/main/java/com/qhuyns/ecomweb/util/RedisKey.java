package com.qhuyns.ecomweb.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum RedisKey {
    TOP_NEWEST_PROD("topNewestProduct",3600)
    ;

    private final String key;
    private final int ttl;
}
