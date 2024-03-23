package com.samcomo.dbz.chat.util;

import static com.samcomo.dbz.global.exception.ErrorCode.ACCESS_DENIED_CHATROOM;
import static com.samcomo.dbz.global.exception.ErrorCode.CHATROOM_NOT_FOUND;

import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatUtils {

  private final ChatRoomRepository chatRoomRepository;

  // 채팅방 검증 + 회원이 채팅방에 존재하는지 검증
  public ChatRoom verifyChatRoomAndMember(String chatRoomId, String memberId) {

    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(CHATROOM_NOT_FOUND));

    // 채팅방에 내가 존재하는지 확인
    if (!isMemberInChatRoom(memberId, chatRoom)) {
      throw new ChatException(ACCESS_DENIED_CHATROOM);
    }
    return chatRoom;
  }

  public void verifyChatRoom(String chatRoomId) {
    chatRoomRepository.findById(chatRoomId).orElseThrow(
        () -> new ChatException(CHATROOM_NOT_FOUND));
  }

  private static boolean isMemberInChatRoom(String memberId, ChatRoom chatRoom) {
    return chatRoom.getMemberIdList().contains(memberId);
  }
}
