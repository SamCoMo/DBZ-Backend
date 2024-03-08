package com.samcomo.dbz.chat.service.impl;

import com.samcomo.dbz.chat.dto.ChatRoomDto;
import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import com.samcomo.dbz.chat.service.ChatRoomService;
import com.samcomo.dbz.chat.util.ChatUtils;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final MemberRepository memberRepository;
  private final ChatUtils chatUtils;


  @Override
  @Transactional
  public ChatRoomDto createOrGetChatRoom(String senderId, String recipientId) {
    // 회원 검증
    chatUtils.verifyMember(senderId);
    chatUtils.verifyMember(recipientId);

    // 채팅방 ID 생성
    String chatRoomId = generateRoomId(senderId, recipientId);
    Set<String> memberIdList = new HashSet<>(Arrays.asList(senderId, recipientId));

    // 채팅방이 없을시 새로 생성
    return ChatRoomDto.from(chatRoomRepository.findById(chatRoomId)
        .orElseGet(() -> {
          ChatRoom chatRoom = ChatRoom.builder()
              .chatRoomId(chatRoomId)
              .memberIdList(memberIdList)
              .lastChatMessageContent(null)
              .lastChatMessageAt(null)
              .build();
          return chatRoomRepository.save(chatRoom);
        }));
  }

  @Transactional(readOnly = true)
  public List<ChatRoomDto> getChatRoomsFromMember(String memberId) {
    // 회원 검증
    memberRepository.findById(Long.parseLong(memberId))
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    // 채팅방 리스트 불러오기 ( 최신 업데이트된 메시지 순서 )
    return chatRoomRepository.findByMemberIdSortedByLastChatMessageAtDesc(
            memberId)
        .stream().map(ChatRoomDto::from)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ChatRoomDto updateLastChatInfo(String chatRoomId, String memberId) {
    // 회원, 채팅방 검증
    ChatRoom chatRoom = chatUtils.verifyChatRoomAndMember(chatRoomId, memberId);

    // 마지막 채팅 조회
    ChatMessage chatMessage
        = chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(
            chatRoomId)
        .orElseThrow(() -> new ChatException(ErrorCode.CHATMESSAGE_NOT_FOUND));

    // 마지막 채팅내역, 채팅시간 업데이트
    chatRoom.setLastChatMessageContent(chatMessage.getContent());
    chatRoom.setLastChatMessageAt(chatMessage.getCreatedAt());

    return ChatRoomDto.from(chatRoomRepository.save(chatRoom));
  }

  @Override
  @Transactional
  public void deleteChatRoom(String chatRoomId, String memberId) {
    // 회원, 채팅방 검증
    ChatRoom chatRoom = chatUtils.verifyChatRoomAndMember(chatRoomId, memberId);

    // 채팅방 삭제
    chatRoomRepository.delete(chatRoom);
    log.info("deleted chatRoom : {}", chatRoomId);
  }

  @Override
  public void deleteChatRoomIfEmptyMessage(String chatRoomId, String memberId) {
    // 회원, 채팅방 검증
    ChatRoom chatRoom = chatUtils.verifyChatRoomAndMember(chatRoomId, memberId);

    // 메시지 조회
    List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(chatRoomId);

    // 채팅밥
    if (chatMessageList.isEmpty()) {
      chatRoomRepository.delete(chatRoom);
      log.info("Deleted Empty chatRoom : {}", chatRoomId);
    }

    log.info("deleteChatRoomIfEmptyMessage : chatRoom : {} : is not Empty", chatRoomId);
  }

  // 사용자 ID 조합으로 채팅방 키 생성
  private String generateRoomId(String senderId, String recipientId) {
    // 아이디 배열로 변환 후 정렬  ( ChatRoomId 중복 생성방지 )
    String[] ids = {senderId, recipientId};
    Arrays.sort(ids);
    return ids[0] + "_" + ids[1];
  }
}
