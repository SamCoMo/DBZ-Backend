package com.samcomo.dbz.chat.model.entity;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatRoom {
  @Id
  private String chatRoomId;
  private Set<String> memberEmailList;
  private String recipientId;
  private String lastChatMessageContent;
  private LocalDateTime lastChatMessageAt;
}
