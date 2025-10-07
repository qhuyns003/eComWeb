package com.ecomweb.order_service.controller;


import com.ecomweb.order_service.dto.response.ApiResponse;
import com.ecomweb.order_service.dto.response.ReviewProductResponse;
import com.ecomweb.order_service.dto.response.ReviewStatResponse;
import com.ecomweb.order_service.service.CustomerReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer_reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerReviewController {

    CustomerReviewService customerReviewService;
    @GetMapping("/{id}")
    ApiResponse<List<ReviewProductResponse>> getAllReview(@PathVariable String id) {
        return ApiResponse.<List<ReviewProductResponse>>builder()
                .result(customerReviewService.getAllReview(id))
                .build();
    }

    // param danh cho thanh loc tim kiem, con pathvariable danh cho dinh danh
    @GetMapping("/stat/{id}")
    ApiResponse<ReviewStatResponse> countReviewByRating(@PathVariable String id) {
        return ApiResponse.<ReviewStatResponse>builder()
                .result(customerReviewService.countReviewByRating(id))
                .build();
    }



}
