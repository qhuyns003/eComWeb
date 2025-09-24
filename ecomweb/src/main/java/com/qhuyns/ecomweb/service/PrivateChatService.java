package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.CreateRoomRequest;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.PrivateChat;
import com.qhuyns.ecomweb.entity.Room;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.entity.key.PrivateChatKey;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.feignClient.IdentityFeignClient;
import com.qhuyns.ecomweb.feignClient.ShopFeignClient;
import com.qhuyns.ecomweb.mapper.UserRoomKeyMapper;
import com.qhuyns.ecomweb.mapper.UserRoomMapper;
import com.qhuyns.ecomweb.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PrivateChatService {
    RoomRepository  roomRepository;
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
        String user2Id = shopFeignClient.getShopIdByUserId(userId1).getResult();
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
