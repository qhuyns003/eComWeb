package com.ecomweb.message_service.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisKey {
    ROLLBACK_TO_SELLER("userDataBeforeUpgradeToSeller",3600),
    ;

    private final String key;
    private final int ttl;
}
