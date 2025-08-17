package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.CreateRoomRequest;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.Room;
import com.qhuyns.ecomweb.entity.User;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.UserRoomKeyMapper;
import com.qhuyns.ecomweb.mapper.UserRoomMapper;
import com.qhuyns.ecomweb.repository.RoomRepository;
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

public class RoomService {
    UserRepository userRepository;
    RoomRepository roomRepository;
    UserRoomService userRoomService;
    RoomMemberService roomMemberService;
    public void create(CreateRoomRequest createRoomRequest,Room room) {
       if(room == null){
           room = Room.builder()
                   .name(createRoomRequest.getName())
                   .createdAt(LocalDateTime.now())
                   .roomId(UUID.randomUUID().toString())
                   .build();
           roomRepository.save(room);
       };
        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
                        .orElseThrow(()->new  AppException(ErrorCode.USER_NOT_EXISTED));
        userRoomService.create(user.getId(),room.getRoomId());
        roomMemberService.create(user.getId(),room.getRoomId());
        for(String userId : createRoomRequest.getMembers()) {
            userRoomService.create(userId,room.getRoomId());
            roomMemberService.create(userId,room.getRoomId());
        };

    }



}
