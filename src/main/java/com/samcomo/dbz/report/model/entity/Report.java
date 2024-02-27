package com.samcomo.dbz.report.model.entity;

import com.samcomo.dbz.global.entity.BaseEntity;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import com.samcomo.dbz.report.model.dto.ReportForm;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @Enumerated(EnumType.STRING)
  private PetType petType;

  @Enumerated(EnumType.STRING)
  private ReportStatus reportStatus;

  private String title;
  private String petName;
  private String species;
  private Integer age;
  private String description;
  private String feature;
  private String streetAddress;
  private String roadAddress;
  private double latitude;
  private double longitude;
  private Long views;
  private Boolean showsPhone;

  public static Report from(ReportForm reportForm, Member member){
    return Report.builder()
        .member(member)
        .petType(reportForm.getPetType())
        .reportStatus(ReportStatus.PUBLISHED)
        .title(reportForm.getTitle())
        .petName(reportForm.getPetName())
        .species(reportForm.getSpecies())
        .age(reportForm.getAge())
        .description(reportForm.getDescriptions())
        .feature(reportForm.getFeature())
        .streetAddress(reportForm.getStreetAddress())
        .roadAddress(reportForm.getRoadAddress())
        .latitude(reportForm.getLatitude())
        .views(0L)
        .showsPhone(reportForm.isShowsPhone())
        .build();
  }
}
