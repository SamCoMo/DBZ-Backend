package com.samcomo.dbz.notification.model.dto;

import com.samcomo.dbz.notification.model.constants.NotificationType;
import com.samcomo.dbz.notification.model.entity.Notification;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class NotificationDto {

  private String memberId;
  private NotificationType type;
  private String message;
  private LocalDateTime createdAt;

  public static NotificationDto fromEntity(Notification notification) {
    return NotificationDto.builder()
        .memberId(notification.getMemberId())
        .type(notification.getType())
        .message(notification.getMessage())
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
