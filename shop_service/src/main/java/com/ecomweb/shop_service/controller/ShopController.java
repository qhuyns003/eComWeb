package com.ecomweb.shop_service.controller;



import com.ecomweb.shop_service.dto.request.ShopCreateRequest;
import com.ecomweb.shop_service.dto.request.ShopUpdateRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.ShopResponse;
import com.ecomweb.shop_service.service.ShopService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
// trailing slash: cơ chế giúp cho các path được khai báo ở ...mapping("../") được đồng nhất
// ví dụ /shop/ và /shop coi như là 1, controller đều bắt đc, tuy nhiên "" và "/" là ngoại lệ, k coi là 1
// với các service sử dụng context path mặc định thêm "/" vào cuối, nếu controller có "/.." rồi thì bỏ qua
// => bestpractice luôn thêm "/" vào path không nên dể "" vì dễ gặp lỗi forward sang mapping của get


// trong java 2 method phan biet voi nhau boi ham va tham so
// => tuy nhien spring chap nhan dieu nay, chi can kac  url va http method
// requestbody, param mac dinh la required, neu k co se bi loi tru khi set false
public class ShopController {

    ShopService shopService;

    @PostMapping("/create")
    ApiResponse<?> create(@RequestBody ShopCreateRequest shopCreateRequest) throws Exception {
        shopService.create(shopCreateRequest);
        return ApiResponse.builder()
                .result("create successful")
                .build();
    }

    @PutMapping("/")
    ApiResponse<?> update(@RequestBody ShopUpdateRequest shopUpdateRequest) {
        shopService.update(shopUpdateRequest);
        return ApiResponse.builder()
                .result("update successful")
                .build();
    }

    @GetMapping
    ApiResponse<?> getInfo() {
        return ApiResponse.builder()
                .result(shopService.getInfo())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<?> getInfoById(@PathVariable String id) {
        return ApiResponse.builder()
                .result(shopService.getInfoById(id))
                .build();
    }








}
