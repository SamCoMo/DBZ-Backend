package com.samcomo.dbz.pin.dto;

import com.samcomo.dbz.pin.model.entity.Pin;
import com.samcomo.dbz.pin.model.entity.PinImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class CreatePinDto {

  @Getter
  @Builder
  public static class Request {
    private String description;

    private List<MultipartFile> multipartFileList;

    private LocalDateTime foundAt;

    private Double latitude;

    private Double longitude;
  }

  @Getter
  @Builder
  public static class Response{
    private Long pinId;

    private Long reportId;

    private Long memberId;

    private String description;

    private LocalDateTime foundAt;

    private String streetAddress; // 생성시 null

    private String roadAddress; // 생성시 null

    private Double latitude;

    private Double longitude;

    private List<PinImageDto> pinImageDtoList;
    public static Response from(Pin pin, List<PinImage> pinImageList){
      return Response.builder()
          .pinId(pin.getPinId())
          .reportId(pin.getReport().getId())
          .memberId(pin.getMember().getId())
          .description(pin.getDescription())
          .foundAt(pin.getFoundAt())
          .streetAddress(pin.getStreetAddress())
          .roadAddress(pin.getRoadAddress())
          .latitude(pin.getLatitude())
          .longitude(pin.getLongitude())
          .pinImageDtoList(pinImageList.stream()
              .map(PinImageDto::from)
              .collect(Collectors.toList()))
          .build();
    }
  }
}
