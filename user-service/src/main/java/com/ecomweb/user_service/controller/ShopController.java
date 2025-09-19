//package com.ecomweb.shop_service.controller;
//
//
//
//import com.ecomweb.shop_service.dto.request.ShopUpdateRequest;
//import com.ecomweb.shop_service.dto.response.ApiResponse;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//// trailing slash: cơ chế giúp cho các path được khai báo ở ...mapping("../") được đồng nhất
//// ví dụ /shop/ và /shop coi như là 1, controller đều bắt đc, tuy nhiên "" và "/" là ngoại lệ, k coi là 1
//// với các service sử dụng context path mặc định thêm "/" vào cuối, nếu controller có "/.." rồi thì bỏ qua
//// => bestpractice luôn thêm "/" vào path không nên dể "" vì dễ gặp lỗi forward sang mapping của get
//
//
//// trong java 2 method phan biet voi nhau boi ham va tham so
//// => tuy nhien spring chap nhan dieu nay, chi can kac  url va http method
//// requestbody, param mac dinh la required, neu k co se bi loi tru khi set false
//public class ShopController {
//
//    ShopService shopService;
//
//    @PostMapping("/create")
//    ResponseEntity<ApiResponse<?>> create(@RequestBody ShopCreateRequest shopCreateRequest) throws Exception {
//        ApiResponse<?> apiResponse = shopService.create(shopCreateRequest);
//        return ResponseEntity.status(apiResponse.getHttpStatus())
//                .body(apiResponse);
//    }
//
//    @PutMapping("/")
//    ResponseEntity<ApiResponse<?>> update(@RequestBody ShopUpdateRequest shopUpdateRequest) {
//        ApiResponse<?> apiResponse=shopService.update(shopUpdateRequest);
//        return ResponseEntity
//                .status(apiResponse.getHttpStatus())
//                .body(apiResponse);
//    }
//
//    @GetMapping
//    ResponseEntity<ApiResponse<?>> getInfo() {
//        ApiResponse<?> apiResponse = shopService.getInfo();
//        return ResponseEntity
//                .status(apiResponse.getHttpStatus())
//                .body(apiResponse);
//    }
//
//    @GetMapping("/{id}")
//    ResponseEntity<ApiResponse<?>> getInfoById(@PathVariable String id) {
//        ApiResponse<?> apiResponse = shopService.getInfoById(id);
//        return ResponseEntity
//                .status(apiResponse.getHttpStatus())
//                .body(apiResponse);
//    }
//
//
//
//
//
//
//
//
//}
