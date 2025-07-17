package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.*;
import com.qhuyns.ecomweb.entity.CustomerReview;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.*;
import com.qhuyns.ecomweb.repository.CustomerReviewRepository;
import com.qhuyns.ecomweb.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerReviewService {

    CustomerReviewRepository customerReviewRepository;
     CustomerReviewMapper customerReviewMapper;
     UserMapper userMapper;
     ProductVariantMapper productVariantMapper;
     DetailAttributeMapper detailAttributeMapper;
    public List<ReviewProductResponse> getAllReview(String id) {
        List<CustomerReview> customerReview = customerReviewRepository.findAllWithUserAndProductVariantAndProductDetail(id);
        List<ReviewProductResponse> reviewProductResponses = new ArrayList<>();
        for (CustomerReview cr : customerReview) {
            ReviewProductResponse reviewProductResponse = customerReviewMapper.toReviewProductResponse(cr);
            reviewProductResponse.setUser(userMapper.toUserResponse(cr.getOrderItem().getOrderShopGroup().getOrder().getUser()));
            reviewProductResponse.setProductVariant(productVariantMapper.toProductVariantResponse(cr.getOrderItem().getProductVariant()));
            reviewProductResponse.getProductVariant().setDetailAttributes(cr.getOrderItem().getProductVariant().getDetailAttributes().stream().map(
                    da -> detailAttributeMapper.toDetailAttributeResponse(da)).collect(Collectors.toList()));
            reviewProductResponses.add(reviewProductResponse);
        }
        return reviewProductResponses;
    }

    public ReviewStatResponse countReviewByRating(String id) {
        List<Object[]> rs = customerReviewRepository.countReviewByRating(id);
        TreeMap<String, Long> ratingCount = new TreeMap<>();
        Long total =0L;
        Double avrRating = 0.0;
        for (Object[] row : rs) {
            ratingCount.put(String.valueOf(row[0]), (Long) row[1]);
            total += (Long) row[1];
            avrRating += ((Long) row[1])* ((Integer)row[0])   ;
        }
        avrRating = (1.0 * avrRating) / total;
        return new ReviewStatResponse().builder()
                .avrRating(avrRating)
                .total(total)
                .stat(ratingCount)
                .build();
    }





}
