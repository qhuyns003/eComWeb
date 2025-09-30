package com.ecomweb.message_service.service;

import com.ecomweb.message_service.dto.response.UserResponse;
import com.ecomweb.message_service.dto.response.UserRoomResponse;
import com.ecomweb.message_service.entity.UserRoom;
import com.ecomweb.message_service.entity.key.UserRoomKey;
import com.ecomweb.message_service.feignClient.IdentityFeignClient;
import com.ecomweb.message_service.mapper.UserRoomKeyMapper;
import com.ecomweb.message_service.mapper.UserRoomMapper;
import com.ecomweb.message_service.repository.UserRoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class UserRoomService {
    UserRoomRepository userRoomRepository;
    UserRoomMapper userRoomMapper;
    UserRoomKeyMapper userRoomKeyMapper;
    IdentityFeignClient identityFeignClient;

//    public void updateLastTime(String roomId) {
//        UserRoom userRoom = userRoomRepository.findByKeyUserIdAndKeyRoomId(SecurityContextHolder.getContext().getAuthentication().getName(), roomId);
//        userRoom.getKey().setLastMessageAt(LocalDateTime.now());
//        userRoomRepository.save(userRoom);
//    }

    public void create(String userId,String roomId,String privateRoomName) {
        userRoomRepository.save(UserRoom.builder()
                        .joinedAt(LocalDateTime.now())
                        .roomName(privateRoomName)
                        .seen(false)
                        .key(UserRoomKey.builder()
                                .userId(userId)
                                .roomId(roomId)
                                .lastMessageAt(LocalDateTime.now())
                                .build())
                .build());

    }

    public List<UserRoomResponse> getRoomByUserId() {
        UserResponse user = identityFeignClient.getActivatedUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .getResult();
        List<UserRoom> userRooms = userRoomRepository.findByKeyUserIdOrderByKeyLastMessageAtDesc(user.getId());
        List<UserRoomResponse> userRoomResponseList = userRooms.stream().map(userRoom -> {
                    UserRoomResponse userRoomResponse = userRoomMapper.toUserRoomResponse(userRoom);
                    userRoomResponse.setKey(userRoomKeyMapper.toUserRoomKeyResponse(userRoom.getKey()));
                    return userRoomResponse;
                })
                .collect(Collectors.toList());
        return userRoomResponseList;
    }

}
