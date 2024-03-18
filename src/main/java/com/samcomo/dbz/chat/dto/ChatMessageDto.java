package com.samcomo.dbz.chat.dto;

import com.samcomo.dbz.chat.model.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class ChatMessageDto {

  @Getter
  @Builder
  public static class Request{
    private String content;
    private List<String> imageBase64List;
  }

  @Getter
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
          .senderId(chatMessage.getSenderId())
          .content(chatMessage.getContent())
          .imageUrlList(chatMessage.getImageUrlList())
          .createdAt(chatMessage.getCreatedAt())
          .build();
    }
  }
}
