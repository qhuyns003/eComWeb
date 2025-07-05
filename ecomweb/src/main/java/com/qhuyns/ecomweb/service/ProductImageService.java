package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.ProductImageResponse;
import com.qhuyns.ecomweb.dto.response.ProductOverviewResponse;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.mapper.ProductImageMapper;
import com.qhuyns.ecomweb.mapper.ProductMapper;
import com.qhuyns.ecomweb.repository.ProductImageRepository;
import com.qhuyns.ecomweb.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageService {

    ProductImageRepository productImageRepository;
    ProductImageMapper productImageMapper;
    public List<ProductImageResponse> getAllByProductId(String productId) {
        return productImageRepository.findByProductId(productId).stream().map(img -> productImageMapper.toProductImageResponse(img)).collect(Collectors.toList());
    }
}
