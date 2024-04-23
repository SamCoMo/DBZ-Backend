package com.samcomo.dbz.notification.model.entity;

import static lombok.AccessLevel.PROTECTED;

import com.samcomo.dbz.notification.model.constants.NotificationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@Builder
@Document
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class Notification {

  @Id
  private String id;
  private String memberId;
  private NotificationType type;
  private String message;
  private LocalDateTime createdAt;
}
