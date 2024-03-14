package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.Pin;
import lombok.Builder;
import lombok.Getter;

public class UpdatePinAddressDto {
  @Getter
  @Builder
  public static class Request {

    private String streetAddress;

    private String roadAddress;

  }

  @Getter
  @Builder
  public static class Response {

    private Long pinId;

    private String streetAddress;

    private String roadAddress;

    private Double latitude;

    private Double longitude;

    public static UpdatePinAddressDto.Response from(Pin pin){
      return Response.builder()
          .pinId(pin.getPinId())
          .roadAddress(pin.getRoadAddress())
          .streetAddress(pin.getStreetAddress())
          .latitude(pin.getLatitude())
          .longitude(pin.getLongitude())
          .build();
    }
  }
}
