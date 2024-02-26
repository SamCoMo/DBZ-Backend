package com.samcomo.dbz.report.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportStatus {
  PUBLISHED("게시됨"),
  DELETED("삭제됨"),
  FOUND("찾음");

  private final String description;
}
