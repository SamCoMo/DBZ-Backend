package com.samcomo.dbz.chat.service;

import com.samcomo.dbz.chat.model.dto.ChatRoomDto;
import java.util.List;

public interface ChatRoomService {

  // 채팅방 가져오기 (채팅방 없을시 생성)
  ChatRoomDto createOrGetChatRoom(String senderId, String recipientId);

  // 사용자가 속한 모든 채팅방 리스트 조회
  List<ChatRoomDto> getChatRoomsFromMember(String memberId);

  // 채팅방 업데이트 : 마지막 채팅내역, 마지막으로 생성된 채팅시각
  ChatRoomDto updateLastChatInfo(String chatRoomId, String memberId);

  // 채팅방 삭제
  void deleteChatRoom(String chatRoomId, String memberId);

  // 채팅방에 메시지 존재하지 않을시 채팅방삭제 (채팅방 나올때)
  void deleteChatRoomIfEmptyMessage(String chatRoomId, String memberId);
}
