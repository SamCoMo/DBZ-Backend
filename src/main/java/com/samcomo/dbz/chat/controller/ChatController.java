package com.samcomo.dbz.chat.controller;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.chat.dto.ChatRoomDto;
import com.samcomo.dbz.chat.service.impl.ChatRoomServiceImpl;
import com.samcomo.dbz.chat.service.impl.ChatServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatRoomServiceImpl chatRoomServiceImpl;
  private final ChatServiceImpl chatServiceImpl;

  // 채팅방 생성, 채팅방 정보 가져오기
  @PostMapping("/chatrooms")
  public ResponseEntity<ChatRoomDto> createChatRoom(
      Authentication authentication,
      @RequestParam String recipientId) {

//    TODO : Member 정보 가져오기
//    String senderId = Authentication.getId();
    String senderId = "suh"; // FIXME : 임시 ID

    ChatRoomDto chatRoomDto
        = chatRoomServiceImpl.createOrGetChatRoom(senderId, recipientId);
    return ResponseEntity.ok(chatRoomDto);
  }

  // 회원 -> 채팅방 목록 조회
  @GetMapping("/member/chatrooms")
  public ResponseEntity<List<ChatRoomDto>> getChatRoomsForMember(
      Authentication authentication) {
//    TODO : Member 정보 가져오기
//    String senderId = Authentication.getId();
    String senderId = "suh"; // FIXME : 임시 ID

    List<ChatRoomDto> chatRoomDtoList
        = chatRoomServiceImpl.getChatRoomsFromMember(senderId);

    return ResponseEntity.ok(chatRoomDtoList);
  }

  // 채팅방 -> 채팅내역 조회
  @GetMapping("/chatrooms/{chatRoomId}/messages")
  public ResponseEntity<List<ChatMessageDto.Response>> getChatMessageList(
      Authentication authentication,
      @PathVariable String chatRoomId){
//    TODO : Member 정보 가져오기
//    String senderId = Authentication.getId();
    String senderId = "suh"; // FIXME : 임시 ID

    List<ChatMessageDto.Response> chatMessageDtoList
        = chatServiceImpl.getChatMessageList(chatRoomId, senderId);

    return ResponseEntity.ok(chatMessageDtoList);

  }

  // 웹소켓 : 메시지 전송
  @MessageMapping("/chatrooms/{chatRoomId}/messages")
  public ResponseEntity<ChatMessageDto.Response> sendMessage(
      Authentication authentication,
      @PathVariable String chatRoomId,
      @ModelAttribute ChatMessageDto.Request request) {
//    TODO : Member 정보 가져오기
//    String senderId = Authentication.getId();
    String senderId = "suh"; // FIXME : 임시 ID

    ChatMessageDto.Response response
        = chatServiceImpl.sendMessage(chatRoomId, senderId, request);

    return ResponseEntity.ok(response);
  }

  // 채팅방 업데이트
  @PutMapping("/chatrooms/{chatRoomId}")
  public ResponseEntity<ChatRoomDto> updateLastChatMessageInfo(
      Authentication authentication,
      @PathVariable String chatRoomId){
//    TODO : Member 정보 가져오기
//    String senderId = Authentication.getId();
    String senderId = "suh"; // FIXME : 임시 ID

    ChatRoomDto chatRoomDto = chatRoomServiceImpl.updateChatRoomInfo(chatRoomId);
    return ResponseEntity.ok(chatRoomDto);
  }
}
