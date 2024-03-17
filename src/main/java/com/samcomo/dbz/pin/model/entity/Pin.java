package com.samcomo.dbz.pin.model.entity;

import static lombok.AccessLevel.PROTECTED;

import com.samcomo.dbz.global.entity.BaseEntity;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.report.model.entity.Report;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class Pin extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pinId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Report report;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Member member;

  @Setter
  private String description;

  @Setter
  private LocalDateTime foundAt;

  private String address;

  private Double latitude;

  private Double longitude;
}
