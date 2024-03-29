package com.samcomo.dbz.notification.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class SendChatDto {

  private static final String DEFAULT_TITLE = "님으로부터 온 채팅";
  private static final String DEFAULT_BODY = " (...)";

  private String title;
  private String body;
  private String fcmToken;

  private SendChatDto(String memberNickname, String body, String fcmToken) {
    this.title = memberNickname + DEFAULT_TITLE;
    this.body = body + DEFAULT_BODY;
    this.fcmToken = fcmToken;
  }

  public static SendChatDto from(String memberNickname, String body, String fcmToken) {
    return new SendChatDto(memberNickname, body, fcmToken);
  }
}
