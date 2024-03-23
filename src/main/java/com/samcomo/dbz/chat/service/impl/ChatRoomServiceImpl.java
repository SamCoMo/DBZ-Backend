package com.samcomo.dbz.chat.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.CHAT_MESSAGE_NOT_FOUND;

import com.samcomo.dbz.chat.exception.ChatException;
import com.samcomo.dbz.chat.model.dto.ChatRoomDto;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.entity.ChatRoom;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.chat.model.repository.ChatRoomRepository;
import com.samcomo.dbz.chat.service.ChatRoomService;
import com.samcomo.dbz.chat.util.ChatUtils;
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
  private final ChatUtils chatUtils;

  @Override
  @Transactional
  public ChatRoomDto createOrGetChatRoom(String memberId, String recipientId) {

    String chatRoomId = generateRoomId(memberId, recipientId);
    Set<String> memberEmailList = new HashSet<>(Arrays.asList(memberId, recipientId));

    // 채팅방이 없을시 새로 생성
    return ChatRoomDto.from(chatRoomRepository.findById(chatRoomId)
        .orElseGet(() -> {
          ChatRoom chatRoom = ChatRoom.builder()
              .chatRoomId(chatRoomId)
              .memberIdList(memberEmailList)
              .lastMessageContent(null)
              .lastMessageSentAt(null)
              .build();
          return chatRoomRepository.save(chatRoom);
        }));
  }

  @Transactional(readOnly = true)
  public List<ChatRoomDto> getChatRoomsFromMember(String memberId) {
    // 채팅방 리스트 불러오기 ( 최신 업데이트된 메시지 순서 )
    return chatRoomRepository.findByMemberIdSortedByLastChatMessageAtDesc(memberId)
        .stream().map(ChatRoomDto::from)
        .collect(Collectors.toList());
  }

  @Override
  public ChatRoomDto updateLastChatInfo(String chatRoomId, String memberId) {

    ChatRoom chatRoom = chatUtils.verifyChatRoomAndMember(chatRoomId, memberId);

    // 마지막 채팅 조회
    ChatMessage chatMessage =
        chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoomId)
            .orElseThrow(() -> new ChatException(CHAT_MESSAGE_NOT_FOUND));

    chatRoom.setLastMessageContent(chatMessage.getContent());
    chatRoom.setLastMessageSentAt(chatMessage.getCreatedAt());

    return ChatRoomDto.from(chatRoomRepository.save(chatRoom));
  }

  @Override
  @Transactional
  public void deleteChatRoom(String chatRoomId, String memberId) {

    ChatRoom chatRoom = chatUtils.verifyChatRoomAndMember(chatRoomId, memberId);

    chatRoomRepository.delete(chatRoom);
    log.info("Deleted chatRoom : {}", chatRoomId);
  }

  @Override
  public void deleteChatRoomIfEmptyMessage(String chatRoomId, String memberId) {

    ChatRoom chatRoom = chatUtils.verifyChatRoomAndMember(chatRoomId, memberId);

    List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(chatRoomId);

    if (chatMessageList.isEmpty()) {
      chatRoomRepository.delete(chatRoom);
      log.info("Deleted Empty chatRoom : {}", chatRoomId);
    }
    log.info("deleteChatRoomIfEmptyMessage : chatRoom : {} : is not Empty", chatRoomId);
  }

  // 사용자 이메일 조합으로 채팅방 키 생성
  private String generateRoomId(String senderId, String recipientId) {
    // 이메일 배열로 변환 후 정렬  ( ChatRoomId 중복 생성방지 )
    String[] IdList = {senderId, recipientId};
    Arrays.sort(IdList);
    return IdList[0] + "_" + IdList[1];
  }
}
