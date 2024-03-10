package com.samcomo.dbz.member.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequestDto {

  @NotBlank(message = "이메일은 필수 항목 입니다.")
  @Pattern(regexp = "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$",
            message = "올바르지 않은 이메일 형식 입니다.")
  private String email;

  @NotBlank(message = "닉네임은 필수 항목 입니다.")
  @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$",
            message = "특수문자를 제외한 2~10자 사이로 입력해주세요.")
  private String nickname;

  @NotBlank(message = "전화번호는 필수 항목 입니다.")
  @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
            message = "올바르지 않은 전화번호 형식 입니다.")
  private String phone;

  @NotBlank(message = "비밀번호는 필수 항목 입니다.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "영문자+특수문자+숫자를 포함하여 8자 이상 입력해주세요.")
  private String password;
}

