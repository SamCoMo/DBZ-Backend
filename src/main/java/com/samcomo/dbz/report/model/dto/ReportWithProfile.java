package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.dto.ReportImageDto.Response;
import com.samcomo.dbz.report.model.entity.Report;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReportWithProfile {

  private Long reportId;
  private WriterProfile writerProfile;
  private String title;
  private String petName;
  private PetType petType;
  private String species;
  private String descriptions;
  private String streetAddress;
  private String roadAddress;
  private Double latitude;
  private Double longitude;
  private String phone;
  private Boolean showsPhone;
  private Long views;
  private ReportStatus reportStatus;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<Response> imageList;

  public static ReportWithProfile from(Report report,
      List<ReportImageDto.Response> reportImageResponseList, WriterProfile profile) {
    return ReportWithProfile.builder()
        .reportId(report.getId())
        .writerProfile(profile)
        .title(report.getTitle())
        .petName(report.getPetName())
        .petType(report.getPetType())
        .species(report.getSpecies())
        .descriptions(report.getDescription())
        .streetAddress(report.getStreetAddress())
        .roadAddress(report.getRoadAddress())
        .latitude(report.getLatitude())
        .longitude(report.getLongitude())
        .phone(report.getShowsPhone() ? report.getMember().getPhone() : "X")
        .showsPhone(report.getShowsPhone())
        .views(report.getViews())
        .reportStatus(report.getReportStatus())
        .createdAt(report.getCreatedAt())
        .updatedAt(report.getUpdatedAt())
        .imageList(reportImageResponseList)
        .build();
  }
}
