package com.samcomo.dbz.report.model.dto;

import lombok.Builder;
import lombok.Getter;

public class ReportStateDto {

  @Getter
  @Builder
  public static class Response {

    private Long reportId;
    private String status;
  }
}
