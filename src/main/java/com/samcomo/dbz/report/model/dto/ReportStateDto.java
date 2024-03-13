package com.samcomo.dbz.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ReportStateDto {

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response{
    private Long reportId;
    private String status;

  }

}
