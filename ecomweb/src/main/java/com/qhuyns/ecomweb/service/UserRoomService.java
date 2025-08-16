package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.UserRoomKeyResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.User;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.UserRoomKeyMapper;
import com.qhuyns.ecomweb.mapper.UserRoomMapper;
import com.qhuyns.ecomweb.repository.MessageRepository;
import com.qhuyns.ecomweb.repository.UserRepository;
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
    UserRepository userRepository;
    UserRoomMapper userRoomMapper;
    UserRoomKeyMapper userRoomKeyMapper;
    public void updateLastTime(String roomId) {
        UserRoom userRoom = userRoomRepository.findByKeyUserIdAndKeyRoomId(SecurityContextHolder.getContext().getAuthentication().getName(), roomId);
        userRoom.getKey().setLastMessageAt(LocalDateTime.now());
        userRoomRepository.save(userRoom);
    }

    public List<UserRoomResponse> getRoomByUserId() {
        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
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
