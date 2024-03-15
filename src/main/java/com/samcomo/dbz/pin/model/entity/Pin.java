package com.samcomo.dbz.pin.model.entity;

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

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
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

  private String description;

  private LocalDateTime foundAt;

  private String streetAddress;

  private String roadAddress;

  private Double latitude;

  private Double longitude;

}
