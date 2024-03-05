package com.samcomo.dbz.chat.service;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.chat.dto.ChatMessageDto.Response;
import java.util.List;

public interface ChatService {
  // 채팅 보내기
  ChatMessageDto.Response sendMessage(String chatRoomId, String senderId, ChatMessageDto.Request request);

  // 채팅방Id -> 채팅 메시지 목록 가져오기
  List<Response> getChatMessageList(String chatRoomId, String senderId);
}
