package com.samcomo.dbz.member.model.dto;

import static lombok.AccessLevel.PRIVATE;

import com.samcomo.dbz.global.utils.annotation.EmailCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class RegisterRequest {

  private static final String REQUIRED_FIELD_MESSAGE = "은(는) 필수 항목입니다.";

  @EmailCheck
  private String email;

  @NotBlank(message = "닉네임" + REQUIRED_FIELD_MESSAGE)
  @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$",
            message = "특수문자를 제외한 2~10자 사이로 입력해주세요.")
  private String nickname;

  @NotBlank(message = "전화번호" + REQUIRED_FIELD_MESSAGE)
  @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
            message = "올바르지 않은 전화번호 형식입니다.")
  private String phone;

  @NotBlank(message = "비밀번호" + REQUIRED_FIELD_MESSAGE)
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "영문자+특수문자+숫자를 포함하여 8자 이상 입력해주세요.")
  private String password;

  @NotNull(message = "주소" + REQUIRED_FIELD_MESSAGE)
  private Double latitude;
  @NotNull(message = "위도" + REQUIRED_FIELD_MESSAGE)
  private Double longitude;
  @NotNull(message = "경도" + REQUIRED_FIELD_MESSAGE)
  private String address;
}

