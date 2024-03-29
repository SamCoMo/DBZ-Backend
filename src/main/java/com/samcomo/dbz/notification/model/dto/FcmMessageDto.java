package com.samcomo.dbz.notification.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class FcmMessageDto {

  private boolean validateOnly;
  private Message message;

  @Builder
  @Getter
  public static class Message {
    private Notification notification;
    private String token;
    private String topic;
  }

  @Builder
  @Getter
  public static class Notification {
    private String title;
    private String body;
  }
}
