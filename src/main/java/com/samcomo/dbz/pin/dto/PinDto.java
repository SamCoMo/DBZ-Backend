package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.Pin;
import com.samcomo.dbz.pin.model.entity.PinImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PinDto {
  private Long pinId;

  private Long reportId;

  private Long memberId;

  private String description;

  private LocalDateTime foundAt;

  private String address;

  private Double latitude;

  private Double longitude;

  private List<PinImageDto> pinImageDtoList;

  public static PinDto from(Pin pin, List<PinImage> pinImageList){
    return PinDto.builder()
        .pinId(pin.getPinId())
        .reportId(pin.getReport().getId())
        .memberId(pin.getMember().getId())
        .description(pin.getDescription())
        .foundAt(pin.getFoundAt())
        .address(pin.getAddress())
        .latitude(pin.getLatitude())
        .longitude(pin.getLongitude())
        .pinImageDtoList(pinImageList.stream()
            .map(PinImageDto::from)
            .collect(Collectors.toList()))
        .build();
  }
}
