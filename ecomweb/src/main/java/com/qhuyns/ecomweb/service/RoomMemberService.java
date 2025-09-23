package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.RoomMember;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.entity.key.RoomMemberKey;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.UserRoomKeyMapper;
import com.qhuyns.ecomweb.mapper.UserRoomMapper;
import com.qhuyns.ecomweb.repository.RoomMemberRepository;
import com.qhuyns.ecomweb.repository.UserRoomRepository;
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

public class RoomMemberService {
    RoomMemberRepository roomMemberRepository;

    public void create(String userId,String roomId) {
       roomMemberRepository.save(RoomMember.builder()
                       .joinedAt(LocalDateTime.now())
                       .key(RoomMemberKey.builder()
                               .roomId(roomId)
                               .userId(userId)
                               .build())
               .build());
    }



}
