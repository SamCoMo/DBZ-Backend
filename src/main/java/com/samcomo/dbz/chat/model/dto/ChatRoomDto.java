package com.samcomo.dbz.chat.model.dto;

import com.samcomo.dbz.chat.model.entity.ChatRoom;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomDto {

  private String chatRoomId;
  private Set<String> memberIdList;
  private String lastMessageContent;
  private LocalDateTime lastMessageSentAt;

  public static ChatRoomDto from(ChatRoom chatRoom) {
    return ChatRoomDto.builder()
        .chatRoomId(chatRoom.getChatRoomId())
        .memberIdList(chatRoom.getMemberIdList())
        .lastMessageContent(chatRoom.getLastMessageContent())
        .lastMessageSentAt(chatRoom.getLastMessageSentAt())
        .build();
  }
}
