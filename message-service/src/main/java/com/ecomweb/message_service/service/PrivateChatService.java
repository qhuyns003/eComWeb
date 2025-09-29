package com.ecomweb.message_service.service;

import com.ecomweb.message_service.dto.response.UserResponse;
import com.ecomweb.message_service.entity.PrivateChat;
import com.ecomweb.message_service.entity.Room;
import com.ecomweb.message_service.entity.key.PrivateChatKey;
import com.ecomweb.message_service.feignClient.IdentityFeignClient;
import com.ecomweb.message_service.feignClient.ShopFeignClient;
import com.ecomweb.message_service.repository.PrivateChatRepository;
import com.ecomweb.message_service.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PrivateChatService {
    RoomRepository roomRepository;
    PrivateChatRepository privateChatRepository;
    RoomService roomService;
    UserRoomService userRoomService;
    RoomMemberService roomMemberService;
    SimpMessagingTemplate messagingTemplate;
    IdentityFeignClient identityFeignClient;
    ShopFeignClient shopFeignClient;
    public String getRoomId(String user1,String user2) {
        PrivateChat pc = privateChatRepository.findByKeyUser1AndKeyUser2(user1,user2);
        if (pc != null) {
            return pc.getRoomId();
        }
        return null;
    }

    public String create(String shopId) {

        UserResponse user1 = identityFeignClient
                .getActivatedUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .getResult();
        String userId1 = user1.getId();
        String user2Id = shopFeignClient.getUserIdByShopId(shopId).getResult();
        UserResponse user2 = identityFeignClient.getActivatedUser(user2Id).getResult();
        String userId2 = user2.getId();
        if(userId1.compareToIgnoreCase(userId2) >0){
            String tmp =userId1;
            userId1 =userId2;
            userId2 =tmp;
        }
        Room room = Room.builder()
                .createdAt(LocalDateTime.now())
                .roomId(UUID.randomUUID().toString())
                .build();
        roomRepository.save(room);

        userRoomService.create(user1.getId(),room.getRoomId(),user2.getFullName());
        roomMemberService.create(userId1,room.getRoomId());
        userRoomService.create(user2.getId(),room.getRoomId(),user1.getFullName());
        roomMemberService.create(userId2,room.getRoomId());

        messagingTemplate.convertAndSendToUser(user1.getUsername(), "/queue/chat-rooms", room.getRoomId());
        messagingTemplate.convertAndSendToUser(user2.getUsername(), "/queue/chat-rooms", room.getRoomId());
        messagingTemplate.convertAndSendToUser(user1.getUsername(), "/queue/chat-notification", "");
        messagingTemplate.convertAndSendToUser(user2.getUsername(), "/queue/chat-notification", "");

        privateChatRepository.save(PrivateChat.builder()
                        .key(PrivateChatKey.builder()
                                .user1(userId1)
                                .user2(userId2)
                                .build())
                        .roomId(room.getRoomId())
                .build());
        return room.getRoomId();
    }




}
