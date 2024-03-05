package com.samcomo.dbz.chat.service.impl;

import com.samcomo.dbz.chat.dto.ChatRoomDto;
import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import com.samcomo.dbz.chat.service.ChatRoomService;
import com.samcomo.dbz.global.exception.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;


  @Override
  @Transactional
  public ChatRoomDto createOrGetChatRoom(String senderId, String recipientId) {
    // 채팅방 ID 생성
    String chatRoomId = generateRoomId(senderId, recipientId);

    // 채팅방이 없을시 새로 생성
    return ChatRoomDto.from(chatRoomRepository.findById(chatRoomId)
        .orElseGet(() -> {
          ChatRoom chatRoom = ChatRoom.builder()
              .chatRoomId(chatRoomId)
              .senderId(senderId)
              .recipientId(recipientId)
              .lastChatMessageContent(null)
              .lastChatMessageAt(null)
              .build();
          return chatRoomRepository.save(chatRoom);
        }));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChatRoomDto> getChatRoomsFromMember(String memberId) {
    return chatRoomRepository.findBySenderIdOrRecipientId(memberId, memberId)
        .stream().map(ChatRoomDto::from)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ChatRoomDto updateChatRoomInfo(String chatRoomId) {
    // 채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));

    // 마지막 채팅 조회
    ChatMessage chatMessage
        = chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATMESSAGE_NOT_FOUND));

    // 마지막 채팅내역, 채팅시간 업데이트
    chatRoom.setLastChatMessageContent(chatMessage.getContent());
    chatRoom.setLastChatMessageAt(chatMessage.getCreatedAt());

    return ChatRoomDto.from(chatRoomRepository.save(chatRoom));
  }

  // 사용자 ID 조합으로 채팅방 키 생성
  private String generateRoomId(String senderId, String recipientId) {
    // 아이디 배열로 변환 후 정렬  ( ChatRoomId 중복 생성방지 )
    String[] ids = {senderId, recipientId};
    Arrays.sort(ids);
    return ids[0] + "_" + ids[1];
  }
}
