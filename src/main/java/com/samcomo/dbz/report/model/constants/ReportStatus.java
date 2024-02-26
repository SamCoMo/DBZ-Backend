package com.samcomo.dbz.report.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportStatus {
  PUBLISHED("진행중"),
  DELETED("삭제됨"),
  FOUND("완료");

  private final String description;
}
