package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.Pin;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class UpdatePinDto {

  @Getter
  @Builder
  public static class Request {

    private String description;

    private LocalDateTime foundAt;
  }

  @Getter
  @Builder
  public static class Response {

    private Long pinId;

    private String description;

    private LocalDateTime foundAt;

    public static UpdatePinDto.Response from(Pin pin){
      return Response.builder()
          .pinId(pin.getPinId())
          .description(pin.getDescription())
          .foundAt(pin.getFoundAt())
          .build();
    }
  }
}
