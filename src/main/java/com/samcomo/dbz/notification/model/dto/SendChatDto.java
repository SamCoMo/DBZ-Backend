package com.samcomo.dbz.notification.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendChatDto {

  private static final String DEFAULT_TITLE = "님으로부터 온 채팅";
  private static final String DEFAULT_BODY = " (...)";

  private String title;
  private String body;
  private String token;

  public SendChatDto(String member, String body, String token) {
    this.title = member + DEFAULT_TITLE;
    this.body = body + DEFAULT_BODY;
    this.token = token;
  }

}
