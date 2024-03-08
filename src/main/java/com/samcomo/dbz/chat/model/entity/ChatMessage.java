package com.samcomo.dbz.chat.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatMessage {
  @Id
  private String chatMessageId;
  private String chatRoomId;
  private String senderId;
  private String content;
  private List<String> imageUrlList;
  private LocalDateTime createdAt;
}

