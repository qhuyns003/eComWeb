package com.qhuyns.ecomweb.controller;

import com.qhuyns.ecomweb.dto.request.MessageRequest;
import com.qhuyns.ecomweb.dto.request.UserRoomKeyRequest;
import com.qhuyns.ecomweb.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

// chi dung cntroller
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
// k bi bat chan boi security, co prefix la ws-app
public class MessageWebSocketController {
    MessageService messageService;

    // FE gửi tin nhắn qua WebSocket tới /ws-app/chat.sendMessage
    // tối ưu hơn rest vì nhanh hơn, k cần thời gian phản hồi
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);
        // Không cần return, BE sẽ broadcast lại qua SimpMessagingTemplate trong service
    }

    // do khong trai qua filter nen token k duoc decode va luu vao secu -> dung thang principle cua ws ta da config
    @MessageMapping("/chat/mark-read")
    public void markRead(@Payload UserRoomKeyRequest  userRoomKeyRequest, Principal principal) {
        messageService.markRead(userRoomKeyRequest,principal);

    }


}
