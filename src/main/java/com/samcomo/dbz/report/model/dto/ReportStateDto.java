package com.samcomo.dbz.report.model.dto;

import lombok.Builder;
import lombok.Getter;


public class ReportStateDto {

  @Getter
  @Builder
  public static class Response{
    private long reportId;
    private String status;

  }

}
