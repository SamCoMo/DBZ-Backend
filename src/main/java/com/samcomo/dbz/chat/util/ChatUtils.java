package com.samcomo.dbz.chat.util;

import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatUtils {
  private final ChatRoomRepository chatRoomRepository;
  private final MemberRepository memberRepository;

  // 채팅방과 회원이 채팅방에 존재하는지 검증
  public ChatRoom verifyChatRoomAndMember(String chatRoomId, String memberId) {
    // 회원 검증
    memberRepository.findById(Long.parseLong(memberId))
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    // 채팅방 조회
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));

    // 채팅방에 내가 존재하는지 확인
    if (!chatRoom.getMemberIdList().contains(memberId)) {
      throw new ChatException(ErrorCode.ACCESS_DENIED_CHATROOM);
    }
    return chatRoom;
  }

  public void verifyChatRoom(String chatRoomId){
    chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
  }

  // 회원 검증
  public Member verifyMember(String memberId){
    return memberRepository.findById(Long.parseLong(memberId))
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
  }

}
