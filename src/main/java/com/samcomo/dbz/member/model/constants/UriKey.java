package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UriKey {

  REISSUE("/member/reissue"),
  LOGIN("/member/login");

  private final String uri;
}
