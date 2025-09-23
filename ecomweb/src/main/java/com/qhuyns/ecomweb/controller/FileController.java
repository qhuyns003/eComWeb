package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.ProductRequest;
import com.qhuyns.ecomweb.dto.response.ProductDetailResponse;
import com.qhuyns.ecomweb.dto.response.ProductOverviewResponse;
import com.qhuyns.ecomweb.dto.response.ProductResponse;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.service.FileService;
import com.qhuyns.ecomweb.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileController {


    FileService fileService;
    @PostMapping("/upload_image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return new ApiResponse<>().<String>builder()
                .result(fileService.uploadImage(file))
                .build();
    }

    @DeleteMapping("/")
    public ApiResponse<String> deleteImage(@RequestParam("url") String url) {
        fileService.deleteImage(url.substring(ImagePrefix.IMAGE_PREFIX.length()));
        return new ApiResponse<>().<String>builder()
                .result("success")
                .build();
    }
}
