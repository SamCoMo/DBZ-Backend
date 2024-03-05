package com.samcomo.dbz.member.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

  MEMBER("ROLE_MEMBER"),
  ADMIN("ROLE_ADMIN");

  private final String key;

  public static MemberRole get(String role) {

    switch (role) {

      case "ROLE_MEMBER" -> {
        return MEMBER;
      }
      case "ROLE_ADMIN" -> {
        return ADMIN;
      }
      default -> {
        return null;
      }
    }
  }
}

