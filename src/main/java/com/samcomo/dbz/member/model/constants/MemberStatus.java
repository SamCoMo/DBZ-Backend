package com.samcomo.dbz.member.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberStatus {
  DELETED("삭제됨"),
  INACTIVE("활성화되지않음"),
  ACTIVE("활성화됨");

  private final String description;
}
