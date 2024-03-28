package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParameterKey {

  ID("id"),
  EMAIL("email"),
  ROLE("role"),
  FCM_TOKEN("fcmToken"),
  PASSWORD("password"),
  COOKIE("Set-Cookie");

  private final String key;
}
