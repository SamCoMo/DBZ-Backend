package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.Pin;
import lombok.Builder;
import lombok.Getter;

public class UpdatePinAddressDto {
  @Getter
  @Builder
  public static class Request {

    private String address;

  }

  @Getter
  @Builder
  public static class Response {

    private Long pinId;

    private String address;

    private Double latitude;

    private Double longitude;

    public static UpdatePinAddressDto.Response from(Pin pin){
      return Response.builder()
          .pinId(pin.getPinId())
          .address(pin.getAddress())
          .latitude(pin.getLatitude())
          .longitude(pin.getLongitude())
          .build();
    }
  }
}
