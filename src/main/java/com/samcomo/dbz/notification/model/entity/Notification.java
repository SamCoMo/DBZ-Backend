package com.samcomo.dbz.notification.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Notification {
  @Id
  private String notificationId;
  private String memberId;
  private String type;
  private String message;
  private LocalDateTime createdAt;

}
