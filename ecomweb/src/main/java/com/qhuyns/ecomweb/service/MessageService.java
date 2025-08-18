package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.CartRequest;
import com.qhuyns.ecomweb.dto.request.MessageRequest;
import com.qhuyns.ecomweb.dto.response.CartResponse;
import com.qhuyns.ecomweb.dto.response.MessageResponse;
import com.qhuyns.ecomweb.entity.*;
import com.qhuyns.ecomweb.entity.key.MessagePrimaryKey;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.MessageMapper;
import com.qhuyns.ecomweb.mapper.MessagePrimaryKeyMapper;
import com.qhuyns.ecomweb.mapper.ShopMapper;
import com.qhuyns.ecomweb.repository.*;
import com.qhuyns.ecomweb.repository.UserRepository;
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

public class MessageService {
    MessageRepository messageRepository;
    UserRoomRepository userRoomRepository;
    RoomMemberRepository roomMemberRepository;
    PrivateChatService privateChatService;
    MessageMapper messageMapper;
    MessagePrimaryKeyMapper messagePrimaryKeyMapper;
    SimpMessagingTemplate  messagingTemplate;
    UserRepository  userRepository;

    public void saveMessage(MessageRequest messageRequest) {
        String sendername = userRepository.findByIdAndActive(messageRequest.getSender(),true)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED))
                .getFullName();
        Message message = Message.builder()
                .content(messageRequest.getContent())
                .type(messageRequest.getType())
                .sender(messageRequest.getSender())
                .sendername(sendername)
                .key(MessagePrimaryKey.builder()
                        .messageId(UUID.randomUUID().toString())
                        .sentAt(LocalDateTime.now())
                        .roomId(messageRequest.getKey().getRoomId())
                        .build())
                .build();
        messageRepository.save(message);
        List<RoomMember> roomMembers = roomMemberRepository.findByKeyRoomId(message.getKey().getRoomId());
        for(RoomMember roomMember : roomMembers){
            List<UserRoom> userRooms = userRoomRepository.findByKeyUserId(roomMember.getKey().getUserId());
            for(UserRoom userRoom : userRooms){
                if(messageRequest.getKey().getRoomId().equals(userRoom.getKey().getRoomId())){
                    userRoomRepository.delete(userRoom);
                    // thay tgian la khoa chinh nen phai xoa ban ghi cu
                    // nen thiet ke thoi gian kp khoa chinh, con viec sap xep nen de java lam
                    userRoom.getKey().setLastMessageAt(LocalDateTime.now());
                    userRoomRepository.save(userRoom);
                }
            }


        }

        MessageResponse messageResponse = messageMapper.toMessageResponse(message);
        messageResponse.setKey(messagePrimaryKeyMapper.toMessagePrimaryKeyResponse(message.getKey()));
        // gui tin nhan moi
        messagingTemplate.convertAndSend("/topic/room." + messageRequest.getKey().getRoomId(),messageResponse);
        // cap nhat dsach room cua user khi co tb moi
        for (RoomMember roomMember : roomMembers) {
            User user = userRepository.findById(roomMember.getKey().getUserId())
                    .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
            messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/chat-rooms", messageRequest.getKey().getRoomId());
        }
    }

    public List<MessageResponse> getMessagesByRoomId(String roomId) {
        List<Message> message = messageRepository.findByKeyRoomIdOrderByKeySentAtAsc(roomId);
        List<MessageResponse> messageResponses = message.stream().map(m -> {
            MessageResponse messageResponse = messageMapper.toMessageResponse(m);
            messageResponse.setKey(messagePrimaryKeyMapper.toMessagePrimaryKeyResponse(m.getKey()));
            return messageResponse;

        }).collect(Collectors.toList());

        return messageResponses;
    }
}
