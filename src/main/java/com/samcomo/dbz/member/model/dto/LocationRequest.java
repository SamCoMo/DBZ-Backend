package com.samcomo.dbz.member.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class LocationRequest {

  private static final String REQUIRED_FIELD_MESSAGE = "는 필수 항목입니다.";

  @NotNull(message = "위도" + REQUIRED_FIELD_MESSAGE)
  private Double latitude;
  @NotNull(message = "경도" + REQUIRED_FIELD_MESSAGE)
  private Double longitude;
  @NotBlank(message = "주소" + REQUIRED_FIELD_MESSAGE)
  private String address;
}
