package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

  ACCESS_TOKEN("accessToken"),
  REFRESH_TOKEN("refreshToken");

  private final String key;
}
