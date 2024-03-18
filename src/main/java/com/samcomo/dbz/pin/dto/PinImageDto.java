package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.PinImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PinImageDto {
  private Long pinImageId;
  private String url;

  public static PinImageDto from(PinImage pinImage){
    return PinImageDto.builder()
        .pinImageId(pinImage.getPinImageId())
        .url(pinImage.getImageUrl())
        .build();
  }
}
