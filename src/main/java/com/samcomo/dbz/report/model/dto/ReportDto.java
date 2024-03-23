package com.samcomo.dbz.report.model.dto;

import static lombok.AccessLevel.PROTECTED;

import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.entity.Report;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportDto {

  @Getter
  @Builder
  @NoArgsConstructor(access = PROTECTED)
  @AllArgsConstructor(access = PROTECTED)
  public static class Form {

    private String title;
    private String petName;
    private PetType petType;
    private String species;
    private String descriptions;
    private String streetAddress;
    private String roadAddress;
    private Double latitude;
    private Double longitude;
    private Boolean showsPhone;
  }

  @Getter
  @Builder
  @NoArgsConstructor(access = PROTECTED)
  @AllArgsConstructor(access = PROTECTED)
  public static class Response {

    private Long reportId;
    private Long memberId;
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
    private boolean showsPhone;
    private Long writerId;
    private Long views;
    private ReportStatus reportStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReportImageDto.Response> imageList;

    public static Response from(Report report,
        List<ReportImageDto.Response> reportImageResponseList, Long writerId) {
      return Response.builder()
          .reportId(report.getId())
          .memberId(report.getMember().getId())
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
          .writerId(writerId)
          .reportStatus(report.getReportStatus())
          .createdAt(report.getCreatedAt())
          .updatedAt(report.getUpdatedAt())
          .imageList(reportImageResponseList)
          .build();
    }
  }
}
