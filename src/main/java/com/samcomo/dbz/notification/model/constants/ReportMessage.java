package com.samcomo.dbz.notification.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportMessage {
  REPORT_MESSAGE("새로운 게시글!",
      "...(확인하러 가기)");

  private final String title;
  private final String body;
}
