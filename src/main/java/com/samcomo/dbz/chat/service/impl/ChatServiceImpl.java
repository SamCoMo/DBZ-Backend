package com.samcomo.dbz.chat.service.impl;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.chat.dto.ChatMessageDto.Response;
import com.samcomo.dbz.chat.service.ChatService;
import com.samcomo.dbz.chat.util.ChatUtils;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.constants.ImageUploadState;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.global.s3.exception.S3Exception;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private final S3Service s3Service;

  @Override
  @Transactional
  public ChatMessageDto.Response sendMessage(String chatRoomId,String senderId,ChatMessageDto.Request request) {
    // 채팅방 검증
    chatUtils.verifyChatRoom(chatRoomId);

    // 이미지 URL 업로드
    List<String> imageUrlList = new ArrayList<>();

    if(request.getImageBase64List() != null && !request.getImageBase64List().isEmpty()){
      for(String base64Image : request.getImageBase64List()){
        ImageUploadState imageUploadState = s3Service.uploadBase64ByStream(base64Image, ImageCategory.CHAT);
        if(imageUploadState.isSuccess()){
          imageUrlList.add(imageUploadState.getImageUrl());
        } else{
          throw new S3Exception(ErrorCode.AWS_SDK_ERROR);
        }
      }
    }

    // 채팅 메시지 생성
    ChatMessage chatMessage = ChatMessage.builder()
        .chatRoomId(chatRoomId)
        .senderId(senderId)
        .content(request.getContent())
        .imageUrlList(imageUrlList)
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
  public Slice<Response> getChatMessageList(String chatRoomId, String senderId, int page, int size) {
    // 채팅방과 회원검증
    chatUtils.verifyChatRoomAndMember(chatRoomId,senderId);

    // 페이징 설정
    Pageable pageable = PageRequest.of(page, size);

    // 채팅 메시지 리스트 슬라이스로 가져오기
    Slice<ChatMessage> chatMessageSlice =  chatMessageRepository.findByChatRoomId(chatRoomId, pageable);

    return chatMessageSlice.map(ChatMessageDto.Response::from);
  }
}
