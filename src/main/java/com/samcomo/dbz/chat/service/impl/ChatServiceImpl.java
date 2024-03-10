package com.samcomo.dbz.chat.service.impl;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.chat.dto.ChatMessageDto.Response;
import com.samcomo.dbz.chat.service.ChatService;
import com.samcomo.dbz.chat.util.ChatUtils;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final ChatMessageRepository chatMessageRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatUtils chatUtils;
//  private final S3Service s3Service;

  @Override
  @Transactional
  public ChatMessageDto.Response sendMessage(String chatRoomId,String senderEmail,ChatMessageDto.Request request) {
    // 채팅방 검증
    chatUtils.verifyChatRoom(chatRoomId);

    //TODO: 이미지 파일을 S3에 업로드하고 URL 을 받아옴

    // 채팅 메시지 생성
    ChatMessage chatMessage = ChatMessage.builder()
        .chatRoomId(chatRoomId)
        .senderEmail(senderEmail)
        .content(request.getContent())
        .imageUrlList(null) //TODO : S3연결 작업필요
        .createdAt(LocalDateTime.now())
        .build();

    // 채팅메시지 저장 , 반환 DTO 정의
    ChatMessageDto.Response chatMessageDto = ChatMessageDto.Response.from(chatMessageRepository.save(chatMessage));

    // 웹소켓 -> 메시지 전송
    messagingTemplate.convertAndSend("/chatrooms/" + chatRoomId, chatMessageDto);

    return chatMessageDto;
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<Response> getChatMessageList(String chatRoomId, String senderEmail, int page, int size) {
    // 채팅방과 회원검증
    chatUtils.verifyChatRoomAndMember(chatRoomId,senderEmail);

    // 페이징 설정
    Pageable pageable = PageRequest.of(page, size);

    // 채팅 메시지 리스트 슬라이스로 가져오기
    Slice<ChatMessage> chatMessageSlice =  chatMessageRepository.findByChatRoomId(chatRoomId, pageable);

    return chatMessageSlice.map(ChatMessageDto.Response::from);
  }
}
