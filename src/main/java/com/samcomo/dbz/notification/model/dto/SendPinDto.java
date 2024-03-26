package com.samcomo.dbz.notification.model.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class SendPinDto {

  private static final String DEFAULT_TITLE = "새로운 핀이 찍혔습니다.";
  private static final String DEFAULT_BODY = "지금 바로 확인해보세요.";

  private String token;
  private String title;
  private String body;

  private SendPinDto(String token) {
    this.title = DEFAULT_TITLE;
    this.body = DEFAULT_BODY;
    this.token = token;
  }

  public static SendPinDto from(String token) {
    return new SendPinDto(token);
  }
}
