package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

  ACCESS_TOKEN("Access-Token", 6000L * 5),  // 30초
  REFRESH_TOKEN("Refresh-Token", 6000L * 10);  // 1분

  private final String key;
  private final Long expiredMs;
}
