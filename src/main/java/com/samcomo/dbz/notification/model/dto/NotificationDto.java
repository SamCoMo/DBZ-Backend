package com.samcomo.dbz.notification.model.dto;

import com.samcomo.dbz.notification.model.constants.NotificationType;
import com.samcomo.dbz.notification.model.entity.Notification;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

  private String memberId;
  private NotificationType type;
  private String message;
  private LocalDateTime createdAt;

  public static NotificationDto fromEntity(Notification noti){

    return NotificationDto.builder()
        .memberId(noti.getMemberId())
        .type(noti.getType())
        .message(noti.getMessage())
        .createdAt(noti.getCreatedAt())
        .build();

  }

}
