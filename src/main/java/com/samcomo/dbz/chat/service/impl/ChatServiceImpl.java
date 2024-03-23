package com.samcomo.dbz.chat.service.impl;

import static com.samcomo.dbz.global.exception.ErrorCode.AWS_SDK_ERROR;

import com.samcomo.dbz.chat.model.dto.ChatMessageDto;
import com.samcomo.dbz.chat.model.dto.ChatMessageDto.Request;
import com.samcomo.dbz.chat.model.dto.ChatMessageDto.Response;
import com.samcomo.dbz.chat.model.entity.ChatMessage;
import com.samcomo.dbz.chat.model.repository.ChatMessageRepository;
import com.samcomo.dbz.chat.service.ChatService;
import com.samcomo.dbz.chat.util.ChatUtils;
import com.samcomo.dbz.global.s3.constants.ImageCategory;
import com.samcomo.dbz.global.s3.constants.ImageUploadState;
import com.samcomo.dbz.global.s3.exception.S3Exception;
import com.samcomo.dbz.global.s3.service.S3Service;
import com.samcomo.dbz.notification.service.NotificationService;
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
  private final NotificationService notificationService;

  private final static String BASE_URI = "/chatrooms/";

  @Override
  @Transactional
  public ChatMessageDto.Response sendMessage(
      String chatRoomId, String senderId, ChatMessageDto.Request request) {

    chatUtils.verifyChatRoom(chatRoomId);

    // 이미지 URL 업로드
    List<String> imageUrlList = new ArrayList<>();
    if (isImageListPresent(request)) {
      for (String base64Image : request.getImageBase64List()) {
        ImageUploadState imageUploadState =
            s3Service.uploadBase64ByStream(base64Image, ImageCategory.CHAT);

        if (imageUploadState.isSuccess()) {
          imageUrlList.add(imageUploadState.getImageUrl());
        } else {
          throw new S3Exception(AWS_SDK_ERROR);
        }
      }
    }

    ChatMessage chatMessage = ChatMessage.builder()
        .chatRoomId(chatRoomId)
        .senderId(senderId)
        .content(request.getContent())
        .imageUrlList(imageUrlList)
        .createdAt(LocalDateTime.now())
        .build();

    ChatMessageDto.Response chatMessageDto =
        ChatMessageDto.Response.from(chatMessageRepository.save(chatMessage));

    // 웹소켓 -> 메시지 전송
    messagingTemplate.convertAndSend(BASE_URI + chatRoomId, chatMessageDto);

    // 채팅 알림 전송
    notificationService.sendChatNotification(chatRoomId, senderId, request);

    return chatMessageDto;
  }

  private static boolean isImageListPresent(Request request) {
    return request.getImageBase64List() != null && !request.getImageBase64List().isEmpty();
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<Response> getChatMessageList(String chatRoomId, String senderId, int page,
      int size) {

    chatUtils.verifyChatRoomAndMember(chatRoomId, senderId);

    Pageable pageable = PageRequest.of(page, size);

    Slice<ChatMessage> chatMessageSlice =
        chatMessageRepository.findByChatRoomId(chatRoomId, pageable);

    return chatMessageSlice.map(ChatMessageDto.Response::from);
  }
}
