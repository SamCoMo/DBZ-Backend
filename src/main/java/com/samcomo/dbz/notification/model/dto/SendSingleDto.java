package com.samcomo.dbz.notification.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class SendSingleDto {

  private String title;
  private String body;
  private String token;

  public static SendSingleDto from(SendPinDto sendPinDto) {
    return SendSingleDto.builder()
        .body(sendPinDto.getBody())
        .title(sendPinDto.getTitle())
        .token(sendPinDto.getToken())
        .build();
  }

  public static SendSingleDto from(SendChatDto sendChatDto) {
    return SendSingleDto.builder()
        .title(sendChatDto.getTitle())
        .body(sendChatDto.getBody())
        .token(sendChatDto.getFcmToken())
        .build();
  }
}
