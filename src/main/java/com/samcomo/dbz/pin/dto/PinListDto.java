package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.Pin;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class PinListDto {

  private Long pinId;

  private String description;

  private LocalDateTime foundAt;

  private String address;

  private Double latitude;

  private Double longitude;

  public static PinListDto from(Pin pin) {
    return PinListDto.builder()
        .pinId(pin.getPinId())
        .description(pin.getDescription())
        .address(pin.getAddress())
        .foundAt(pin.getFoundAt())
        .latitude(pin.getLatitude())
        .longitude(pin.getLongitude())
        .build();
  }
}