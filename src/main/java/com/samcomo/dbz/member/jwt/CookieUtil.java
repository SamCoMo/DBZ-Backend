package com.samcomo.dbz.member.jwt;

import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CookieUtil {

  public final static String SAME_SITE_CONFIG = "None";

  public String createCookie(String refreshToken) {
    return String.valueOf(
        ResponseCookie.from(REFRESH_TOKEN.getKey(), refreshToken)
            .path("/")
            .maxAge(24 * 60 * 60)
            .sameSite(SAME_SITE_CONFIG)
            .httpOnly(true)
            .secure(true)
            .build());
  }

  public String getNullCookie() {
    return String.valueOf(
        ResponseCookie.from(REFRESH_TOKEN.getKey(), null)
            .path("/")
            .maxAge(0)
            .sameSite(SAME_SITE_CONFIG)
            .httpOnly(true)
            .secure(true)
            .build());
  }

  public String getRefreshToken(HttpServletRequest request) {
    String refreshToken = null;

    log.info("[로그아웃 요청] /member/logout");
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(REFRESH_TOKEN.getKey())) {
        log.info("===== Cookie : {} =====", cookie);
        refreshToken = cookie.getValue();
      }
    }
    return refreshToken;
  }
}
