package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.feignClient.IdentityFeignClient;
import com.qhuyns.ecomweb.service.PrivateChatService;
import com.qhuyns.ecomweb.service.UserRoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private_chats")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class PrivateChatController {
    PrivateChatService privateChatService;
    IdentityFeignClient identityFeignClient;
    @GetMapping()
    public ApiResponse<?> getRoomId(@RequestParam String user1,@RequestParam String user2) {
        if(user1.compareToIgnoreCase(user2) >0){
            String tmp =user1;
            user1 =user2;
            user2 =tmp;
        }
        return ApiResponse.builder()
                .result(privateChatService.getRoomId(user1,user2))
                .build();
    }

    @GetMapping("/shop")
    public ApiResponse<?> getRoomIdByShopId(@RequestParam String user1,@RequestParam String shopId) {
        String user2 = identityFeignClient.getUserIdByShopId(shopId).getResult();
        if(user1.compareToIgnoreCase(user2) >0){
            String tmp =user1;
            user1 =user2;
            user2 =tmp;
        }
        return ApiResponse.builder()
                .result(privateChatService.getRoomId(user1,user2))
                .build();
    }

    @PostMapping()
    public ApiResponse<?> createAndGetRoomId(@RequestParam String shopId) {
        return ApiResponse.builder()
                .result(privateChatService.create(shopId))
                .build();
    }




}
