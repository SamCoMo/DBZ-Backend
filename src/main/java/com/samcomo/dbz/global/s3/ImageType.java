package com.samcomo.dbz.global.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageType {

  REPORT("report"),
  PIN("pin"),
  CHAT("chat");

  private final String name;
}
