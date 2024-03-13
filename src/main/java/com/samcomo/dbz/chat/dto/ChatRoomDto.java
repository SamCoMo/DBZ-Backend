package com.samcomo.dbz.chat.dto;

import com.samcomo.dbz.chat.model.entity.ChatRoom;
import java.time.LocalDateTime;
import java.util.Set;
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
  private Set<String> memberEmailList;
  private String lastChatMessageContent;
  private LocalDateTime lastChatMessageAt;

  public static ChatRoomDto from(ChatRoom chatRoom){
    return ChatRoomDto.builder()
        .chatRoomId(chatRoom.getChatRoomId())
        .memberEmailList(chatRoom.getMemberEmailList())
        .lastChatMessageContent(chatRoom.getLastChatMessageContent())
        .lastChatMessageAt(chatRoom.getLastChatMessageAt())
        .build();
  }
}
