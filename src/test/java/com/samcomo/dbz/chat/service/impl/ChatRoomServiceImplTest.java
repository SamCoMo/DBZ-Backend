package com.samcomo.dbz.chat.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samcomo.dbz.chat.dto.ChatRoomDto;
import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private ChatMessageRepository chatMessageRepository;

  @InjectMocks
  private ChatRoomServiceImpl chatRoomServiceImpl;

  @BeforeEach
  void setUp() {
  }

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
  @DisplayName("createOrGetChatRoom: 채팅방이 존재하지않을때 새로 생성 반환 성공")
  void createOrGetChatRoom_WhenChatRoomDoNotExist() {
    // given
    String senderId = "user1";
    String recipientId = "user2";
    String chatRoomId = senderId + "_" + recipientId;
    ChatRoom exampleChatRoom = createExampleChatRoom(chatRoomId, senderId,
        recipientId);

    when(chatRoomRepository.findById(anyString())).thenReturn(Optional.empty());
    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(
        exampleChatRoom);

    //when
    ChatRoomDto chatRoomDto = chatRoomServiceImpl.createOrGetChatRoom(senderId,
        recipientId);

    //then
    verify(chatRoomRepository, times(1)).findById(chatRoomId);
    verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    assertEquals(senderId, chatRoomDto.getSenderId());
    assertEquals(recipientId, chatRoomDto.getRecipientId());

  }

  @Test
  @DisplayName("createOrGetChatRoom: 채팅방이 존재할때 반환 성공")
  void createOrGetChatRoom_WhenChatRoomExist() {
    // given
    String senderId = "member1";
    String recipientId = "member2";
    String chatRoomId = senderId + "_" + recipientId;
    ChatRoom exampleChatRoom = createExampleChatRoom(chatRoomId, senderId, recipientId);

    when(chatRoomRepository.findById(anyString())).thenReturn(
        Optional.of(exampleChatRoom));

    //when
    ChatRoomDto resultChatRoomDto
        = chatRoomServiceImpl.createOrGetChatRoom(senderId, recipientId);

    //then
    verify(chatRoomRepository, times(1)).findById(chatRoomId);
    verify(chatRoomRepository, times(0)).save(any(ChatRoom.class));
    assertEquals(senderId, resultChatRoomDto.getSenderId());
    assertEquals(recipientId, resultChatRoomDto.getRecipientId());
  }

  @Test
  @DisplayName("getChatRoomsFromMember: 채팅방 가져오기 성공")
  void getChatRoomsFromMember() {
    //given
    String senderId = "member1";
    String recipientId1 = "member2";
    String recipientId2 = "member3";
    String exampleChatRoomId = "member1_member2";
    String exampleChatRoom2Id = "member1_member3";

    ChatRoom exampleChatRoom = createExampleChatRoom(exampleChatRoomId,senderId, recipientId1);
    ChatRoom exampleChatRoom2 = createExampleChatRoom(exampleChatRoom2Id,senderId, recipientId2);
    List<ChatRoom> chatRoomList = Arrays.asList(exampleChatRoom,
        exampleChatRoom2);

    when(chatRoomRepository.findBySenderIdOrRecipientId(senderId,
        senderId)).thenReturn(chatRoomList);

    //when
    List<ChatRoomDto> resultChatRoomList = chatRoomServiceImpl.getChatRoomsFromMember(
        senderId);

    //then
    verify(chatRoomRepository, times(1)).findBySenderIdOrRecipientId(senderId,
        senderId);

    assertEquals(2, resultChatRoomList.size());

    assertEquals(senderId, resultChatRoomList.get(0).getSenderId());
    assertEquals(recipientId1, resultChatRoomList.get(0).getRecipientId());

    assertEquals(senderId, resultChatRoomList.get(1).getSenderId());
    assertEquals(recipientId2, resultChatRoomList.get(1).getRecipientId());
  }

  @Test
  @DisplayName("updateChatRoomInfo: 채팅방 업데이트 성공")
  void updateChatRoomInfo() {
    //given
    String senderId = "member1";
    String recipientId = "member2";
    String chatRoomId = senderId + "_" + recipientId;
    String content = "last_message";

    ChatRoom exampleChatRoom
        = createExampleChatRoom(chatRoomId, senderId, recipientId);

    ChatMessage exampleChatMessage
        = createExampleChatMessage(chatRoomId, senderId, recipientId, content);

    ChatRoom updatedChatRoom = ChatRoom.builder()
        .chatRoomId(chatRoomId)
        .senderId(senderId)
        .recipientId(recipientId)
        .lastChatMessageContent(exampleChatMessage.getContent())
        .lastChatMessageAt(exampleChatMessage.getCreatedAt())
        .build();

    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(exampleChatRoom));

    when(chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoomId))
        .thenReturn(Optional.of(exampleChatMessage));


    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(updatedChatRoom);

    //when
    ChatRoomDto resultChatRoomDto = chatRoomServiceImpl.updateChatRoomInfo(chatRoomId);

    //then
    assertNotNull(resultChatRoomDto);
    verify(chatRoomRepository, times(1)).findById(chatRoomId);
    verify(chatMessageRepository, times(1)).findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
    verify(chatRoomRepository, times(1)).save(exampleChatRoom);

    assertEquals(resultChatRoomDto.getLastChatMessageAt(), exampleChatMessage.getCreatedAt());
    assertEquals(resultChatRoomDto.getLastChatMessageContent(), exampleChatMessage.getContent());

    assertEquals(content, resultChatRoomDto.getLastChatMessageContent());
    assertEquals(exampleChatMessage.getCreatedAt(), resultChatRoomDto.getLastChatMessageAt());
    assertEquals(senderId, resultChatRoomDto.getSenderId());
    assertEquals(recipientId, resultChatRoomDto.getRecipientId());
    assertEquals(chatRoomId, resultChatRoomDto.getChatRoomId());
  }

  @Test
  @DisplayName("updateChatRoomInfo: 채팅방이 없을때")
  void updateChatRoomInfo_ChatRoomNotExist(){
    //given
    String senderId = "member1";
    String recipientId = "member2";
    String chatRoomId = senderId + "_" + recipientId;

    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

    //when, then
    assertThrows(ChatException.class, () -> chatRoomServiceImpl.updateChatRoomInfo(chatRoomId));
  }

  @Test
  @DisplayName("updateChatRoomInfo: 채팅방이 없을때")
  void updateChatRoomInfo_ChatRoomExist(){
    //given
    String senderId = "member1";
    String recipientId = "member2";
    String chatRoomId = senderId + "_" + recipientId;

    ChatRoom exampleChatRoom
        = createExampleChatRoom(chatRoomId, senderId, recipientId);

    when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(exampleChatRoom));

    when(chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoomId)).thenReturn(Optional.empty());

    //when, then
    assertThrows(ChatException.class, () -> chatRoomServiceImpl.updateChatRoomInfo(chatRoomId));

  }
}