package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.service.MessageService;
import com.qhuyns.ecomweb.service.UserRoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user_rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class UserRoomController {
    UserRoomService  userRoomService;

    @PostMapping("")
    public ApiResponse<?> create() {
       return ApiResponse.builder()
               .result("success")
               .build();
    }

    @PutMapping("/{roomId}")
    public ApiResponse<?> update(@PathVariable String roomId) {
        userRoomService.updateLastTime(roomId);
        return ApiResponse.builder()
                .result("success")
                .build();
    }


    @GetMapping("")
    public ApiResponse<List<UserRoomResponse>> get() {
        return ApiResponse.<List<UserRoomResponse>>builder()
                .result(userRoomService.getRoomByUserId())
                .build();
    }
}
