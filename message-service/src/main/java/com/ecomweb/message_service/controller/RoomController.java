package com.ecomweb.message_service.controller;


import com.ecomweb.message_service.dto.request.ApiResponse;
import com.ecomweb.message_service.dto.request.CreateRoomRequest;
import com.ecomweb.message_service.service.RoomService;
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
