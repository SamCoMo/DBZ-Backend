package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {

  DEFAULT("samcomo"),
  KAKAO("kakao"),
  GOOGLE("google");

  private final String key;
}
