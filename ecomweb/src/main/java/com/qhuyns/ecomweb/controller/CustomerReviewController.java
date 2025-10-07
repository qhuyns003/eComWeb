//package com.qhuyns.ecomweb.controller;
//
//
//import com.qhuyns.ecomweb.dto.request.ApiResponse;
//import com.qhuyns.ecomweb.dto.response.ProductDetailResponse;
//import com.qhuyns.ecomweb.dto.response.ProductOverviewResponse;
//import com.qhuyns.ecomweb.dto.response.ReviewProductResponse;
//import com.qhuyns.ecomweb.dto.response.ReviewStatResponse;
//import com.qhuyns.ecomweb.service.CustomerReviewService;
//import com.qhuyns.ecomweb.service.ProductService;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/customer_reviews")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class CustomerReviewController {
//
//    CustomerReviewService customerReviewService;
//    @GetMapping("/{id}")
//    ApiResponse<List<ReviewProductResponse>> getAllReview(@PathVariable String id) {
//        return ApiResponse.<List<ReviewProductResponse>>builder()
//                .result(customerReviewService.getAllReview(id))
//                .build();
//    }
//
//    // param danh cho thanh loc tim kiem, con pathvariable danh cho dinh danh
//    @GetMapping("/stat/{id}")
//    ApiResponse<ReviewStatResponse> countReviewByRating(@PathVariable String id) {
//        return ApiResponse.<ReviewStatResponse>builder()
//                .result(customerReviewService.countReviewByRating(id))
//                .build();
//    }
//
//
//
//}
