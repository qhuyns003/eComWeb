package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.CreateRoomRequest;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.PrivateChat;
import com.qhuyns.ecomweb.entity.Room;
import com.qhuyns.ecomweb.entity.User;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.entity.key.PrivateChatKey;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.UserRoomKeyMapper;
import com.qhuyns.ecomweb.mapper.UserRoomMapper;
import com.qhuyns.ecomweb.repository.PrivateChatRepository;
import com.qhuyns.ecomweb.repository.UserRepository;
import com.qhuyns.ecomweb.repository.UserRoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PrivateChatService {
    PrivateChatRepository privateChatRepository;
    RoomService roomService;
    public String getRoomId(String user1,String user2) {
        PrivateChat pc = privateChatRepository.findByKeyUser1AndKeyUser2(user1,user2);
        if (pc != null) {
            return pc.getRoomId();
        }
        return null;
    }

    public void create(String user2) {
        String user1 = SecurityContextHolder.getContext().getAuthentication().getName();
        if(user1.compareToIgnoreCase(user2) >0){
            String tmp =user1;
            user1 =user2;
            user2 =tmp;
        }
        CreateRoomRequest createRoomRequest = CreateRoomRequest.builder()
                .members(List.of(user1,user2))
                .build();
        Room room = Room.builder()
                .name(createRoomRequest.getName())
                .createdAt(LocalDateTime.now())
                .roomId(UUID.randomUUID().toString())
                .build();
        roomService.create(createRoomRequest,room);
        privateChatRepository.save(PrivateChat.builder()
                        .key(PrivateChatKey.builder()
                                .user1(user1)
                                .user2(user2)
                                .build())
                        .roomId(room.getRoomId())
                .build());
    }




}
