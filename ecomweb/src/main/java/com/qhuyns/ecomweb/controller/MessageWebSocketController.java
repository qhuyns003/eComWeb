package com.qhuyns.ecomweb.controller;

import com.qhuyns.ecomweb.dto.request.MessageRequest;
import com.qhuyns.ecomweb.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MessageWebSocketController {
    MessageService messageService;

    // FE gửi tin nhắn qua WebSocket tới /app/chat.sendMessage
    // tối ưu hơn rest vì nhanh hơn, k cần thời gian phản hồi
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);
        // Không cần return, BE sẽ broadcast lại qua SimpMessagingTemplate trong service
    }
}
