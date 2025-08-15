package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.CartRequest;
import com.qhuyns.ecomweb.dto.response.CartResponse;
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
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        // Gán UUID và thời gian gửi nếu cần
        if (message.getKey().getMessageId() == null) {
            message.getKey().setMessageId(UUID.randomUUID());
        }
        if (message.getKey().getSentAt() == null) {
            message.getKey().setSentAt(LocalDateTime.now());
        }
        return ResponseEntity.ok(messageService.saveMessage(message));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String roomId) {
        return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId));
    }
}
