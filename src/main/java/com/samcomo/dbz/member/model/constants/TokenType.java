package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

  ACCESS_TOKEN("Access-Token"),
  REFRESH_TOKEN("Refresh-Token");

  private final String key;
}
