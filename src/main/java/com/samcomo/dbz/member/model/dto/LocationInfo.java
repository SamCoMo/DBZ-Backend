package com.samcomo.dbz.member.model.dto;

import com.samcomo.dbz.member.model.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class LocationInfo {

  private static final String REQUIRED_FIELD_MESSAGE = "는 필수 항목입니다.";

  @Getter
  @Builder
  public static class Request {
    @NotNull(message = "위도" + REQUIRED_FIELD_MESSAGE)
    private Double latitude;
    @NotNull(message = "경도" + REQUIRED_FIELD_MESSAGE)
    private Double longitude;
    @NotBlank(message = "주소" + REQUIRED_FIELD_MESSAGE)
    private String address;
  }

  @Getter
  @Builder
  public static class Response {
    private Long memberId;
    private Double latitude;
    private Double longitude;
    private String address;

    public static Response from(Member member) {
      return Response.builder()
          .memberId(member.getId())
          .address(member.getAddress())
          .latitude(member.getLatitude())
          .longitude(member.getLongitude())
          .build();
    }
  }
}
