package com.samcomo.dbz.report.model.entity;

import com.samcomo.dbz.global.entity.BaseEntity;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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

  @Builder.Default
  @OneToMany
  private List<ReportImage> reportImageList = new ArrayList<>();

  private String title;
  private String petName;
  private String species;
  private Integer age;
  private String description;
  private String streetAddress;
  private String roadAddress;
  private Long latitude;
  private Long longitude;
  private Long views;
  private Boolean showsPhone;

}
