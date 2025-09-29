package com.ecomweb.message_service.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.MessageRequest;
import com.qhuyns.ecomweb.dto.response.MessageResponse;
import com.qhuyns.ecomweb.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
