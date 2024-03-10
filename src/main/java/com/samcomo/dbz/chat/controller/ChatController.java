package com.samcomo.dbz.chat.controller;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.chat.dto.ChatMessageDto.Response;
import com.samcomo.dbz.chat.dto.ChatRoomDto;
import com.samcomo.dbz.chat.service.impl.ChatRoomServiceImpl;
import com.samcomo.dbz.chat.service.impl.ChatServiceImpl;
import com.samcomo.dbz.member.model.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

  // 채팅방 생성, 채팅방 정보 가져오기
  @PostMapping("/room")
  public ResponseEntity<ChatRoomDto> createChatRoom(
      @AuthenticationPrincipal Member member,
      @RequestParam String recipientId
  ) {
    String memberEmail = member.getEmail();
    ChatRoomDto chatRoomDto
        = chatRoomServiceImpl.createOrGetChatRoom(memberEmail, recipientId);
    return ResponseEntity.ok(chatRoomDto);
  }

  // 회원 -> 채팅방 목록 조회
  @GetMapping("/member/room-list")
  public ResponseEntity<List<ChatRoomDto>> getChatRoomsForMember(
      @AuthenticationPrincipal Member sender
  ) {

    String memberEmail = sender.getEmail();

    List<ChatRoomDto> chatRoomDtoList
        = chatRoomServiceImpl.getChatRoomsFromMember(memberEmail);

    return ResponseEntity.ok(chatRoomDtoList);
  }

  // 채팅방 -> 채팅내역 조회
  @GetMapping("/room/{chatRoomId}/message-list")
  public ResponseEntity<Slice<ChatMessageDto.Response>> getChatMessageList(
      @AuthenticationPrincipal Member member,
      @PathVariable String chatRoomId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size
  ) {
    // 회원 불러오기
    String memberEmail = member.getEmail();

    Slice<Response> chatMessageDtoSlice
        = chatServiceImpl.getChatMessageList(chatRoomId,memberEmail,page,size);

    return ResponseEntity.ok(chatMessageDtoSlice);

  }

  // 웹소켓 : 메시지 전송
  @MessageMapping("/room/{chatRoomId}/message")
  public ResponseEntity<ChatMessageDto.Response> sendMessage(
      @AuthenticationPrincipal Member sender,
      @PathVariable String chatRoomId,
      @ModelAttribute ChatMessageDto.Request request) {
    // 회원 불러오기
    String senderEmail = sender.getEmail();

    ChatMessageDto.Response response
        = chatServiceImpl.sendMessage(chatRoomId, senderEmail, request);

    return ResponseEntity.ok(response);
  }

  // 채팅방 업데이트
  @PutMapping("/room-list/{chatRoomId}")
  public ResponseEntity<ChatRoomDto> updateLastChatMessageInfo(
      @AuthenticationPrincipal Member member,
      @PathVariable String chatRoomId){
    // 회원 불러오기
    String memberEmail = member.getEmail();

    ChatRoomDto chatRoomDto = chatRoomServiceImpl.updateLastChatInfo(chatRoomId, memberEmail);
    return ResponseEntity.ok(chatRoomDto);
  }

  // 채팅방 나올때 ChatMessage 존재하지 않을시 삭제
  @DeleteMapping("/room/check-empty/{chatRoomId}")
  public ResponseEntity<Void> deleteChatRoomIfEmptyMessage(
      @AuthenticationPrincipal Member member,
      @PathVariable String chatRoomId){
    // 회원 불러오기
    String memberEmail = member.getEmail();

    chatRoomServiceImpl.deleteChatRoomIfEmptyMessage(chatRoomId,memberEmail);

    return ResponseEntity.noContent().build();
  }

  // 채팅방 삭제
  @DeleteMapping("/room/{chatRoomId}")
  public ResponseEntity<Void> deleteChatRoom(
      @AuthenticationPrincipal Member member,
      @PathVariable String chatRoomId){
    // 회원 불러오기
    String memberEmail = member.getEmail();

    chatRoomServiceImpl.deleteChatRoom(chatRoomId, memberEmail);

    return ResponseEntity.noContent().build();

  }
}
