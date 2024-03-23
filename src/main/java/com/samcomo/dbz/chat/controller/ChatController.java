package com.samcomo.dbz.chat.controller;

import com.samcomo.dbz.chat.model.dto.ChatMessageDto;
import com.samcomo.dbz.chat.model.dto.ChatMessageDto.Response;
import com.samcomo.dbz.chat.model.dto.ChatRoomDto;
import com.samcomo.dbz.chat.service.impl.ChatRoomServiceImpl;
import com.samcomo.dbz.chat.service.impl.ChatServiceImpl;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatRoomServiceImpl chatRoomServiceImpl;
  private final ChatServiceImpl chatServiceImpl;

  @PostMapping("/room")
  public ResponseEntity<ChatRoomDto> createChatRoom(
      @AuthenticationPrincipal MemberDetails details,
      @RequestParam String recipientId
  ) {
    ChatRoomDto chatRoomDto
        = chatRoomServiceImpl.createOrGetChatRoom(details.getIdAsString(), recipientId);

    return ResponseEntity.ok(chatRoomDto);
  }

  @GetMapping("/member/room-list")
  public ResponseEntity<List<ChatRoomDto>> getChatRoomsForMember(
      @AuthenticationPrincipal MemberDetails details
  ) {
    List<ChatRoomDto> chatRoomDtoList =
        chatRoomServiceImpl.getChatRoomsFromMember(details.getIdAsString());

    return ResponseEntity.ok(chatRoomDtoList);
  }

  @GetMapping("/room/{chatRoomId}/message-list")
  public ResponseEntity<Slice<ChatMessageDto.Response>> getChatMessageList(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable String chatRoomId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size
  ) {
    Slice<Response> chatMessageDtoSlice =
        chatServiceImpl.getChatMessageList(chatRoomId, details.getIdAsString(), page, size);

    return ResponseEntity.ok(chatMessageDtoSlice);
  }

  @MessageMapping("/room/{chatRoomId}/message")
  public ResponseEntity<ChatMessageDto.Response> sendMessage(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable String chatRoomId,
      ChatMessageDto.Request request
  ) {
    ChatMessageDto.Response response =
        chatServiceImpl.sendMessage(chatRoomId, details.getIdAsString(), request);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/room-list/{chatRoomId}")
  public ResponseEntity<ChatRoomDto> updateLastChatMessageInfo(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable String chatRoomId
  ) {
    ChatRoomDto chatRoomDto =
        chatRoomServiceImpl.updateLastChatInfo(chatRoomId, details.getIdAsString());

    return ResponseEntity.ok(chatRoomDto);
  }

  @DeleteMapping("/room/check-empty/{chatRoomId}")
  public ResponseEntity<Void> deleteChatRoomIfEmptyMessage(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable String chatRoomId
  ) {
    chatRoomServiceImpl.deleteChatRoomIfEmptyMessage(chatRoomId, details.getIdAsString());

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/room/{chatRoomId}")
  public ResponseEntity<Void> deleteChatRoom(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable String chatRoomId
  ) {
    chatRoomServiceImpl.deleteChatRoom(chatRoomId, details.getIdAsString());

    return ResponseEntity.noContent().build();
  }
}
