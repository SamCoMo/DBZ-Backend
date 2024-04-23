package com.samcomo.dbz.notification.model.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class SendReportDto {

  private static final String DEFAULT_TITLE = "새로운 게시글이 등록되었습니다.";
  private static final String DEFAULT_BODY = "(이어서 보기)";

  private String title;
  private String body;

  private SendReportDto(String description) {
    this.title = DEFAULT_TITLE;
    this.body = description + DEFAULT_BODY;
  }

  public static SendReportDto from(String description) {
    return new SendReportDto(description);
  }
}
