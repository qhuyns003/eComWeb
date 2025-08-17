package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.CartRequest;
import com.qhuyns.ecomweb.dto.response.CartResponse;
import com.qhuyns.ecomweb.entity.*;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.ShopMapper;
import com.qhuyns.ecomweb.repository.*;
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

public class MessageService {
    MessageRepository messageRepository;
    UserRoomRepository userRoomRepository;
    RoomMemberRepository roomMemberRepository;

    public Message saveMessage(Message message) {
        List<RoomMember> roomMembers = roomMemberRepository.findByKeyRoomId(message.getKey().getRoomId());
        for(RoomMember roomMember : roomMembers){
            UserRoom userRoom = userRoomRepository.findByKeyUserIdAndKeyRoomId(roomMember.getKey().getUserId(), roomMember.getKey().getRoomId());
            userRoom.getKey().setLastMessageAt(LocalDateTime.now());
            userRoomRepository.save(userRoom);
        }
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByRoomId(String roomId) {
        return messageRepository.findByKeyRoomIdOrderByKeySentAtAsc(roomId);
    }
}
