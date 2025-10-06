package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.*;
import com.qhuyns.ecomweb.dto.response.*;
import com.qhuyns.ecomweb.entity.*;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.feignClient.IdentityFeignClient;
import com.qhuyns.ecomweb.feignClient.ShopFeignClient;
import com.qhuyns.ecomweb.mapper.*;
import com.qhuyns.ecomweb.repository.*;
import com.qhuyns.ecomweb.util.RedisCacheHelper;
import com.qhuyns.ecomweb.util.RedisKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {
    ProductVariantRepository productVariantRepository;

    @Transactional
    public void updateStock(List<ProductVariantStockUpdateRequest> requests){
        for(ProductVariantStockUpdateRequest rq : requests){
            ProductVariant productVariant = productVariantRepository.findById(rq.getVariantId())
                    .orElseThrow(()-> new AppException(ErrorCode.VARIANT_NOT_FOUND));
            productVariant.setStock(productVariant.getStock()-rq.getQuantity());
            productVariantRepository.save(productVariant);
        };
    };





}
