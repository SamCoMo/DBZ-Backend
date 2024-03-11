package com.samcomo.dbz.member.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequestDto {

  private String email;
  private String nickname;
  private String phone;
  private String password;
}

