package com.samcomo.dbz.member.jwt;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
@AllArgsConstructor(access = PRIVATE)
public class AuthUtils {

  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;

  public static AuthUtils of(JwtUtil jwtUtil, CookieUtil cookieUtil) {
    return new AuthUtils(jwtUtil, cookieUtil);
  }
}
