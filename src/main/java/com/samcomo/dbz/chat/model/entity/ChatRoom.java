package com.samcomo.dbz.chat.model.entity;

import static lombok.AccessLevel.PROTECTED;

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
@Builder
@Document
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class ChatRoom {

  @Id
  private String chatRoomId;
  private Set<String> memberIdList;
  private String recipientId;
  private String lastMessageContent;
  private LocalDateTime lastMessageSentAt;
}
