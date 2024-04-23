package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.Pin;
import com.samcomo.dbz.pin.model.entity.PinImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public class RegisterPinDto {

  @ToString
  @Getter
  @Builder
  public static class Request {

    private String description;

    private List<MultipartFile> multipartFileList;

    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime foundAt;

    private Double latitude;

    private Double longitude;
  }

  @ToString
  @Getter
  @Builder
  public static class Response {

    private Long pinId;

    private Long reportId;

    private Long memberId;

    private String description;

    private String address;

    private LocalDateTime foundAt;

    private Double latitude;

    private Double longitude;

    private List<PinImageDto> pinImageDtoList;

    public static Response from(Pin pin, List<PinImage> pinImageList) {
      return Response.builder()
          .pinId(pin.getPinId())
          .reportId(pin.getReport().getId())
          .memberId(pin.getMember().getId())
          .description(pin.getDescription())
          .address(pin.getAddress())
          .foundAt(pin.getFoundAt())
          .latitude(pin.getLatitude())
          .longitude(pin.getLongitude())
          .pinImageDtoList(pinImageList.stream()
              .map(PinImageDto::from)
              .collect(Collectors.toList()))
          .build();
    }
  }
}
