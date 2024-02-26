package com.samcomo.dbz.pin.model.entity;

import com.samcomo.dbz.global.entity.BaseEntity;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.report.model.entity.Report;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Pin extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Report report;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @Builder.Default
  @OneToMany
  private List<PinImage> pinImageList = new ArrayList<>();

  private String description;

  private LocalDateTime foundAt;

  private String streetAddress;

  private String roadAddress;

  private Long latitude;

  private Long longitude;

}
