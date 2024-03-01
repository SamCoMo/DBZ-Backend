package com.samcomo.dbz.report.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportStatus {
  PUBLISHED("published"),
  DELETED("deleted"),
  FOUND("found");

  private final String description;
}
