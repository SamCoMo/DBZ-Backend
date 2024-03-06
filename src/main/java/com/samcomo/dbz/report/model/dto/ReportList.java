package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.entity.Report;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ReportList {

  private long reportId;
  private String title;
  private PetType petType;
  private String species;
  private ReportStatus reportStatus;
  private Integer age;
  private String petName;
  private String feature;
  private String streetAddress;
  private String roadAddress;
  private double lastDistance;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String imageUrl;

  public static ReportList from(Report report){
    return ReportList.builder()
        .reportId(report.getId())
        .title(report.getTitle())
        .petType(report.getPetType())
        .species(report.getSpecies())
        .reportStatus(report.getReportStatus())
        .age(report.getAge())
        .petName(report.getPetName())
        .feature(report.getFeature())
        .streetAddress(report.getStreetAddress())
        .roadAddress(report.getRoadAddress())
        .createdAt(report.getCreatedAt())
        .updatedAt(report.getUpdatedAt())
        .build();
  }
}
