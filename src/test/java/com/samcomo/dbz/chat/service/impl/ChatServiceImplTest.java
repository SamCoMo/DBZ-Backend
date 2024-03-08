package com.samcomo.dbz.chat.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.chat.dto.ChatMessageDto.Request;
import com.samcomo.dbz.chat.dto.ChatMessageDto.Response;
import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private ChatServiceImpl chatServiceImpl;

  private ChatRoom createExampleChatRoom(
      String chatRoomId, String senderId, String recipientId) {
    return ChatRoom.builder()
        .chatRoomId(chatRoomId)
        .senderId(senderId)
        .recipientId(recipientId)
        .lastChatMessageContent(null)
        .lastChatMessageAt(null)
        .build();
  }

  private ChatMessage createExampleChatMessage(
      String chatRoomId,
      String senderId,
      String recipientId,
      String content) {
    return ChatMessage.builder()
        .id("1")
        .chatRoomId(chatRoomId)
        .senderId(senderId)
        .recipientId(recipientId)
        .imageUrlList(null)
        .content(content)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("sendMessage: 성공")
  void sendMessage_success() {
    //given
    String chatRoomId = "member1_member2";
    String senderId = "member1";
    String recipientId = "member2";
    String content = "exampleContent";

    Request chatMessageRequest = Request
        .builder()
        .content(content)
        .imageUrlList(null)
        .build();

    ChatRoom chatRoom = createExampleChatRoom(chatRoomId, senderId, recipientId);

    ChatMessage chatMessage
        = createExampleChatMessage(chatRoomId, senderId, recipientId, content);

    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

    when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

    //when
    Response resultResponseDto = chatServiceImpl.sendMessage(chatRoomId, senderId, chatMessageRequest);

    //then
    assertNotNull(resultResponseDto);
    assertEquals(chatMessage.getId(), resultResponseDto.getId());
    assertEquals(chatRoomId, resultResponseDto.getChatRoomId());
    assertEquals(senderId, resultResponseDto.getSenderId());
    assertEquals(recipientId, resultResponseDto.getRecipientId());
    assertEquals(content, resultResponseDto.getContent()); //
    assertNull(resultResponseDto.getImageUrlList()); // 이미지 URL 리스트는 null로 가정
    assertEquals(chatMessage.getCreatedAt(), resultResponseDto.getCreatedAt());
    verify(messagingTemplate,times(1)).convertAndSend("/chatrooms/" + chatRoomId, resultResponseDto);
  }

  @Test
  @DisplayName("sendMessage: 실패")
  void sendMessage_ChatRoomNotExist() {
    //given
    String chatRoomId = "member1_member2";
    String senderId = "member1";
    String content = "exampleContent";

    ChatMessageDto.Request chatMessageRequest =
        ChatMessageDto.Request.builder()
            .content(content)
            .imageUrlList(null)
            .build();

    //when
    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

    //then
    assertThrows(ChatException.class,
        () -> chatServiceImpl.sendMessage(chatRoomId, senderId, chatMessageRequest));
  }
}