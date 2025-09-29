package com.ecomweb.message_service.service;

import com.ecomweb.message_service.dto.request.MessageRequest;
import com.ecomweb.message_service.dto.request.UserRoomKeyRequest;
import com.ecomweb.message_service.dto.response.MessageResponse;
import com.ecomweb.message_service.dto.response.UserResponse;
import com.ecomweb.message_service.entity.Message;
import com.ecomweb.message_service.entity.RoomMember;
import com.ecomweb.message_service.entity.UserRoom;
import com.ecomweb.message_service.entity.key.MessagePrimaryKey;
import com.ecomweb.message_service.feignClient.IdentityFeignClient;
import com.ecomweb.message_service.mapper.MessageMapper;
import com.ecomweb.message_service.mapper.MessagePrimaryKeyMapper;
import com.ecomweb.message_service.repository.MessageRepository;
import com.ecomweb.message_service.repository.RoomMemberRepository;
import com.ecomweb.message_service.repository.UserRoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
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
    IdentityFeignClient identityFeignClient;

    // cassandra co hoat dong k ?
    @Transactional
    public void saveMessage(MessageRequest messageRequest) {
        String sendername = identityFeignClient.getActivatedUser(messageRequest.getSender())
                .getResult().getFullName();
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
                    userRoom.setSeen(false);
                    userRoomRepository.save(userRoom);
                }
            }


        }

        MessageResponse messageResponse = messageMapper.toMessageResponse(message);
        messageResponse.setKey(messagePrimaryKeyMapper.toMessagePrimaryKeyResponse(message.getKey()));
        // gui tin nhan moi
        // co the gui broadcast haoc private queue voi tung user (dk la phai subribe user)
        messagingTemplate.convertAndSend("/topic/room." + messageRequest.getKey().getRoomId(),messageResponse);
        // cap nhat dsach room cua user khi co tb moi
        for (RoomMember roomMember : roomMembers) {
            UserResponse user = identityFeignClient.getUser(roomMember.getKey().getUserId()).getResult();
            messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/chat-rooms", messageRequest.getKey().getRoomId());
            messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/chat-notification", messageRequest.getKey().getRoomId());

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

    public void markRead(UserRoomKeyRequest userRoomKeyRequest, Principal principal){
        UserRoom userRoom = userRoomRepository.findByKeyUserIdAndKeyLastMessageAtAndKeyRoomId(userRoomKeyRequest.getUserId(),userRoomKeyRequest.getLastMessageAt(),userRoomKeyRequest.getRoomId());
        userRoom.setSeen(true);
        userRoomRepository.save(userRoom);
        messagingTemplate.convertAndSendToUser(principal.getName()
                , "/queue/chat-notification", "");
    }
}
