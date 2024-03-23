package com.samcomo.dbz.chat.service;

import com.samcomo.dbz.chat.model.dto.ChatMessageDto;
import com.samcomo.dbz.chat.model.dto.ChatMessageDto.Response;
import org.springframework.data.domain.Slice;

public interface ChatService {

  // 채팅 보내기
  ChatMessageDto.Response sendMessage(String chatRoomId, String senderId,
      ChatMessageDto.Request request);

  // 채팅방Id -> 채팅 메시지 목록 가져오기
  Slice<Response> getChatMessageList(String chatRoomId, String senderId, int page, int size);
}