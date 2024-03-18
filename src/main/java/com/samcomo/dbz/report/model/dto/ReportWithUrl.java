package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.report.model.constants.PetType;
import com.samcomo.dbz.report.model.constants.ReportStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportWithUrl {


  private Long id;
  private Long memberId;
  private PetType petType;
  private ReportStatus reportStatus;
  private String title;
  private String petName;
  private String species;
  private String streetAddress;
  private String roadAddress;
  private Double latitude;
  private Double longitude;
  private String imageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;


}
