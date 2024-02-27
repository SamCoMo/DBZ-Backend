package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.report.model.entity.ReportImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReportImageResponse {

  private String url;
  private Long id;

  public static ReportImageResponse from(ReportImage reportImage){
    return ReportImageResponse.builder()
        .url(reportImage.getImageUrl())
        .build();
  }

}
