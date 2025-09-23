package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomKeyResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.feignClient.IdentityFeignClient;
import com.qhuyns.ecomweb.mapper.UserRoomKeyMapper;
import com.qhuyns.ecomweb.mapper.UserRoomMapper;
import com.qhuyns.ecomweb.repository.MessageRepository;
import com.qhuyns.ecomweb.repository.UserRoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    IdentityFeignClient  identityFeignClient;

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
