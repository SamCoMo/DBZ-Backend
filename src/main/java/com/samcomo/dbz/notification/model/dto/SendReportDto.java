package com.samcomo.dbz.notification.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SendReportDto {

  private static final String DEFAULT_TITLE = "새로운 게시글이 등록되었습니다.";
  private static final String DEFAULT_BODY = "(이어서 보기)";

  private String token;
  private String title;
  private String body;

  public SendReportDto(String token, String description) {
    this.title = DEFAULT_TITLE;
    this.body = description + DEFAULT_BODY;
    this.token = token;
  }

}
