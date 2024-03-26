package com.samcomo.dbz.global.s3.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageCategory {

  REPORT("report"),
  PIN("pin"),
  CHAT("chat"),
  MEMBER("member");

  private final String name;
}