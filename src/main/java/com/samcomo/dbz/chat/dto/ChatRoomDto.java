package com.samcomo.dbz.chat.dto;

import com.samcomo.dbz.chat.model.entity.ChatRoom;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
  private String chatRoomId;
  private String senderId;
  private String recipientId;
  private String lastChatMessageContent;
  private LocalDateTime lastChatMessageAt;

  public static ChatRoomDto from(ChatRoom chatRoom){
    return ChatRoomDto.builder()
        .chatRoomId(chatRoom.getChatRoomId())
        .senderId(chatRoom.getSenderId())
        .recipientId(chatRoom.getRecipientId())
        .lastChatMessageContent(chatRoom.getLastChatMessageContent())
        .lastChatMessageAt(chatRoom.getLastChatMessageAt())
        .build();
  }
}
