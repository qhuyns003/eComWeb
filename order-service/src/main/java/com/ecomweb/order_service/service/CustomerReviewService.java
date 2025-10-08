package com.ecomweb.order_service.service;

import com.ecomweb.order_service.dto.response.ProductVariantResponse;
import com.ecomweb.order_service.dto.response.ReviewProductResponse;
import com.ecomweb.order_service.dto.response.ReviewStatResponse;
import com.ecomweb.order_service.entity.CustomerReview;
import com.ecomweb.order_service.feignClient.ProductFeignClient;
import com.ecomweb.order_service.mapper.CustomerReviewMapper;
import com.ecomweb.order_service.repository.CustomerReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     ProductFeignClient productFeignClient;
    public List<ReviewProductResponse> getAllReview(String id) {
        List<String> variantIds = productFeignClient.getVariantIdsByProductId(id).getResult();
        List<CustomerReview> customerReview = customerReviewRepository.findAllWithUserAndProductVariantAndProductDetail(variantIds);
        List<ReviewProductResponse> reviewProductResponses = new ArrayList<>();
        for (CustomerReview cr : customerReview) {
            ReviewProductResponse reviewProductResponse = customerReviewMapper.toReviewProductResponse(cr);
            reviewProductResponse.setUserId(cr.getOrderItem().getOrderShopGroup().getOrder().getUserId());
            ProductVariantResponse productVariantResponse = productFeignClient.getById(cr.getOrderItem().getProductVariantId()).getResult();
            reviewProductResponse.setProductVariant(productVariantResponse);
            reviewProductResponses.add(reviewProductResponse);
        }
        return reviewProductResponses;
    }

    public ReviewStatResponse countReviewByRating(String id) {

        List<String> variantIds = productFeignClient.getVariantIdsByProductId(id).getResult();
        List<Object[]> rs = customerReviewRepository.countReviewByRating(variantIds);
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
