package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.CartRequest;
import com.qhuyns.ecomweb.dto.request.MessageRequest;
import com.qhuyns.ecomweb.dto.response.CartResponse;
import com.qhuyns.ecomweb.dto.response.MessageResponse;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.service.CartService;
import com.qhuyns.ecomweb.service.MessageService;
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
@RequestMapping("/messages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class MessageController {
    MessageService messageService;

    @PostMapping
    public ApiResponse<?> sendMessage(@RequestBody MessageRequest  messageRequest) {
        messageService.saveMessage(messageRequest);
        return ApiResponse.builder()
                .result("success")
                .build();
    }

    @GetMapping("/{roomId}")
    public ApiResponse<List<MessageResponse>> getMessages(@PathVariable String roomId) {
        return  ApiResponse.<List<MessageResponse>>builder()
                .result(messageService.getMessagesByRoomId(roomId))
                .build();
    }
}
