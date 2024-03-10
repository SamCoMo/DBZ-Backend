package com.samcomo.dbz.chat.dto;

import com.samcomo.dbz.chat.model.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class ChatMessageDto {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request{
    private String content;
    private List<MultipartFile> MultipartFileList;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response{
    private String chatMessageId;
    private String chatRoomId;
    private String senderId;
    private String recipientId;
    private String content;
    private List<String> imageUrlList;
    private LocalDateTime createdAt;

    public static Response from(ChatMessage chatMessage){
      return Response.builder()
          .chatMessageId(chatMessage.getChatMessageId())
          .chatRoomId(chatMessage.getChatRoomId())
          .senderId(chatMessage.getSenderEmail())
          .content(chatMessage.getContent())
          .imageUrlList(chatMessage.getImageUrlList())
          .createdAt(chatMessage.getCreatedAt())
          .build();
    }
  }
}
