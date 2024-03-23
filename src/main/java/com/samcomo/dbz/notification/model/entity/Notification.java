package com.samcomo.dbz.notification.model.entity;

import com.samcomo.dbz.notification.model.constants.NotificationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Document
public class Notification {

  @Id
  private String id;

  private String memberId;
  private NotificationType type;
  private String message;
  private LocalDateTime createdAt;
}
