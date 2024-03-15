package com.samcomo.dbz.notification.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmMessageDto {

  private  boolean validateOnly;
  private Message message;

  @Builder
  @AllArgsConstructor
  @Getter
  public static class Message {
    private Notification notification;
    private String token;
    private String topic;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class Notification{
    private String title;
    private String body;
  }
}
