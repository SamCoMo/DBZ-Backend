package com.samcomo.dbz.pin.model.entity;

import static lombok.AccessLevel.PROTECTED;

import com.samcomo.dbz.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class PinImage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pinImageId;

  @Setter
  @Column(nullable = false)
  private String imageUrl;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Pin pin;
}
