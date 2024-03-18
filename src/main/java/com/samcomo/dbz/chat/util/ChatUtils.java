package com.samcomo.dbz.chat.util;

import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import com.samcomo.dbz.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatUtils {
  private final ChatRoomRepository chatRoomRepository;

  // 채팅방 검증 + 회원이 채팅방에 존재하는지 검증
  public ChatRoom verifyChatRoomAndMember(String chatRoomId, String memberId) {
    // 채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));

    // 채팅방에 내가 존재하는지 확인
    if (!chatRoom.getMemberIdList().contains(memberId)) {
      throw new ChatException(ErrorCode.ACCESS_DENIED_CHATROOM);
    }
    return chatRoom;
  }

  // 채팅방 검증
  public void verifyChatRoom(String chatRoomId){
    chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
  }
}
