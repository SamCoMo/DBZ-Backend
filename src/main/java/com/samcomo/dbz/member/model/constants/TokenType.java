package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

  ACCESS_TOKEN("Access-Token", 6000L * 10 * 1000),  // 10분
  REFRESH_TOKEN("Refresh-Token", 6000L * 10 * 60 * 24);  // 24시간

  private final String key;
  private final Long expiredMs;
}
