package com.ecomweb.message_service.service;

import com.ecomweb.message_service.dto.request.CreateRoomRequest;
import com.ecomweb.message_service.dto.response.RoomResponse;
import com.ecomweb.message_service.dto.response.UserResponse;
import com.ecomweb.message_service.entity.Room;
import com.ecomweb.message_service.exception.AppException;
import com.ecomweb.message_service.exception.ErrorCode;
import com.ecomweb.message_service.feignClient.IdentityFeignClient;
import com.ecomweb.message_service.mapper.RoomMapper;
import com.ecomweb.message_service.repository.RoomRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class RoomService {
    RoomRepository roomRepository;
    UserRoomService userRoomService;
    RoomMemberService roomMemberService;
    SimpMessagingTemplate messagingTemplate;
    RoomMapper roomMapper;
    IdentityFeignClient identityFeignClient;
    public void create(CreateRoomRequest createRoomRequest) {

        List<String> members = new ArrayList<>(createRoomRequest.getMembers());

        Room room = Room.builder()
                   .name(createRoomRequest.getName())
                   .createdAt(LocalDateTime.now())
                   .roomId(UUID.randomUUID().toString())
                   .build();
        UserResponse user = identityFeignClient
                .getActivatedUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .getResult();
        members.add(user.getId());

        roomRepository.save(room);

        for(String userId : members) {
            userRoomService.create(userId,room.getRoomId(),null);
            roomMemberService.create(userId,room.getRoomId());
        };
        for (String userId: members) {
            UserResponse user2 = identityFeignClient.getActivatedUser(userId).getResult();
            messagingTemplate.convertAndSendToUser(user2.getUsername(), "/queue/chat-rooms", room.getRoomId());
        }

    }

    public RoomResponse findById(String roomId) {
        return roomMapper.toRoomResponse(roomRepository.findById(roomId)
                .orElseThrow(()-> new AppException(ErrorCode.ROOM_NOT_FOUND)));
    }



}
