package com.samcomo.dbz.report.model.dto;

import static lombok.AccessLevel.PROTECTED;

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
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class ReportSearchSummaryDto {

  private Long reportId;
  private Long memberId;
  private String title;
  private String petName;
  private PetType petType;
  private String species;
  private String streetAddress;
  private String roadAddress;
  private String imageUrl;
  private ReportStatus reportStatus;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ReportSearchSummaryDto from(Report report) {
    return ReportSearchSummaryDto.builder()
        .reportId(report.getId())
        .memberId(report.getMember().getId())
        .title(report.getTitle())
        .petName(report.getPetName())
        .petType(report.getPetType())
        .species(report.getSpecies())
        .streetAddress(report.getStreetAddress())
        .roadAddress(report.getRoadAddress())
        .reportStatus(report.getReportStatus())
        .createdAt(report.getCreatedAt())
        .updatedAt(report.getUpdatedAt())
        .build();
  }
}
