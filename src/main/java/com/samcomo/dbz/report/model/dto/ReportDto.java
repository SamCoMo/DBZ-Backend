package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.entity.Report;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportDto {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Form{
    private String title;
    private PetType petType;
    private boolean showsPhone;
    private String species;
    private Integer age;
    private String descriptions;
    private String petName;
    private String feature;
    private String streetAddress;
    private String roadAddress;
    private double latitude;
    private double longitude;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response{
    private long reportId;
    private long memberId;
    private String phone;
    private String title;
    private PetType petType;
    private boolean showsPhone;
    private String species;
    private Integer age;
    private String descriptions;
    private String petName;
    private String feature;
    private String streetAddress;
    private long views;
    private String roadAddress;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReportImageDto.Response> imageList;

    public static Response from(Report report,
        List<ReportImageDto.Response> reportImageResponseList) {
      return Response.builder()
          .reportId(report.getId())
          .memberId(report.getMember().getId())
          .phone(report.getShowsPhone() ? report.getMember().getPhone() : "X")
          .title(report.getTitle())
          .petType(report.getPetType())
          .showsPhone(report.getShowsPhone())
          .species(report.getSpecies())
          .age(report.getAge())
          .views(report.getViews())
          .descriptions(report.getDescription())
          .petName(report.getPetName())
          .feature(report.getFeature())
          .streetAddress(report.getStreetAddress())
          .roadAddress(report.getRoadAddress())
          .latitude(report.getLatitude())
          .longitude(report.getLongitude())
          .createdAt(report.getCreatedAt())
          .updatedAt(report.getUpdatedAt())
          .imageList(reportImageResponseList)
          .build();
    }
  }

}
