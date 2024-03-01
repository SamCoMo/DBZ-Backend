package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.report.model.entity.ReportImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportImageDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response{
    private String url;
    private Long id;

    public static Response from(ReportImage reportImage){
      return Response.builder()
          .url(reportImage.getImageUrl())
          .build();
    }
  }

}
