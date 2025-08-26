package com.ecomweb.gateway.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisKey {
    TOP_NEWEST_PROD("topNewestProduct",3600),
    TOP_SELLING_PROD("topSellingProduct",3600),
    GET_ALL_CATEGORY("getAllCategory",3600),
    NEWEST_PROD_OF_SHOP("getNewestProdForShop",3600)
    ;

    private final String key;
    private final int ttl;
}
