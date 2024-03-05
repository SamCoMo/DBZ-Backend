package com.samcomo.dbz.chat.service.impl;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.chat.dto.ChatMessageDto.Response;
import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.service.ChatService;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final SimpMessagingTemplate messagingTemplate;
//  private final S3Service s3Service;

  @Override
  @Transactional
  public ChatMessageDto.Response sendMessage(String chatRoomId,String senderId,ChatMessageDto.Request request) {
    // 채팅방 가져오기
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));

    //TODO: 이미지 파일을 S3에 업로드하고 URL 을 받아옴

    // 채팅 메시지 생성
    ChatMessage chatMessage = ChatMessage.builder()
        .chatRoomId(chatRoomId)
        .senderId(senderId)
        .recipientId(chatRoom.getRecipientId())
        .imageUrlList(null) //TODO : S3연결 작업필요
        .createdAt(LocalDateTime.now())
        .build();

    // 채팅메시지 저장 , 반환 DTO 정의
    ChatMessageDto.Response chatMessageDto = ChatMessageDto.Response.from(chatMessageRepository.save(chatMessage));

    // 웹소켓 -> 메시지 전송
    messagingTemplate.convertAndSend("/chatrooms/" + chatRoomId, chatMessageDto);

    return chatMessageDto;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChatMessageDto.Response> getChatMessageList(String chatRoomId, String senderId) {
    // 채팅방 가져오기
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));

    // 채팅방에 속해있는지 확인
    if ( !(senderId.equals(chatRoom.getSenderId()) ||
        senderId.equals(chatRoom.getRecipientId()))){
      throw new ChatException(ErrorCode.ACCESS_DENIED_CHATROOM);
    }

    // 채팅 메시지 리스트 가져오기
    List<ChatMessage> chatMessageList =  chatMessageRepository.findByChatRoomId(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATMESSAGE_NOT_FOUND));

    return chatMessageList.stream()
        .map(Response::from)
        .collect(Collectors.toList());
  }
}
