package com.samcomo.dbz.notification.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PinMessage {
  PIN_MESSAGE("새로운 핀이 찍혔습니다.",
      "지금 바로 확인해보세요.");

  private final String title;
  private final String body;
}
