package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.service.ApolloService;
import com.qhuyns.ecomweb.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/apollo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApolloController {

    ApolloService apolloService;

    @GetMapping("/")
    ApiResponse<?> getCategories() throws IOException, InterruptedException {
        apolloService.get();
        return ApiResponse.builder()
                .result("success")
                .build();
    }
}
