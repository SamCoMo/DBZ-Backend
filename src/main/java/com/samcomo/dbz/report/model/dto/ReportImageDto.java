package com.samcomo.dbz.report.model.dto;

import static lombok.AccessLevel.PROTECTED;

import com.samcomo.dbz.report.model.entity.ReportImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportImageDto {

  @Getter
  @Builder
  @NoArgsConstructor(access = PROTECTED)
  @AllArgsConstructor(access = PROTECTED)
  public static class Response{

    private String url;
    private Long id;

    public static Response from(ReportImage reportImage){
      return Response.builder()
          .id(reportImage.getId())
          .url(reportImage.getImageUrl())
          .build();
    }
  }
}
