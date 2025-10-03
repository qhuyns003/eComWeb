package com.ecomweb.message_service.controller;


import com.ecomweb.message_service.dto.request.ApiResponse;
import com.ecomweb.message_service.dto.response.UserRoomResponse;
import com.ecomweb.message_service.service.UserRoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user_rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class UserRoomController {
    UserRoomService userRoomService;

//    @PostMapping("")
//    public ApiResponse<?> create() {
//       return ApiResponse.builder()
//               .result("success")
//               .build();
//    }

//    @PutMapping("/{roomId}")
//    public ApiResponse<?> update(@PathVariable String roomId) {
//        userRoomService.updateLastTime(roomId);
//        return ApiResponse.builder()
//                .result("success")
//                .build();
//    }


    @GetMapping("")
    public ApiResponse<List<UserRoomResponse>> get() {
        return ApiResponse.<List<UserRoomResponse>>builder()
                .result(userRoomService.getRoomByUserId())
                .build();
    }
}
