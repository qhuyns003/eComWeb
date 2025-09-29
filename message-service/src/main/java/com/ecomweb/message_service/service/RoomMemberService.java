package com.ecomweb.message_service.service;

import com.ecomweb.message_service.entity.RoomMember;
import com.ecomweb.message_service.entity.key.RoomMemberKey;
import com.ecomweb.message_service.repository.RoomMemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
