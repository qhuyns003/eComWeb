package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.CreateRoomRequest;
import com.qhuyns.ecomweb.entity.Room;
import com.qhuyns.ecomweb.service.PrivateChatService;
import com.qhuyns.ecomweb.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class RoomController {
    RoomService roomService;

    @PostMapping()
    public ApiResponse<?> create(@RequestBody CreateRoomRequest createRoomRequest) {
        roomService.create(createRoomRequest);
        return ApiResponse.builder()
                .result("success")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getById(@PathVariable String id) {
        return ApiResponse.builder()
                .result(roomService.findById(id))
                .build();
    }




}
