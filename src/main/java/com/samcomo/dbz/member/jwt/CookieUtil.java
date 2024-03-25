package com.samcomo.dbz.member.jwt;

import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CookieUtil {

  public final static String COOKIE_KEY = "Set-Cookie";

  public String createCookie(String refreshToken) {
    return String.valueOf(
        ResponseCookie.from(REFRESH_TOKEN.getKey(), refreshToken)
            .path("/")
            .maxAge(24 * 60 * 60)
            .sameSite("None")
            .httpOnly(true)
            .secure(true)
            .build());
  }

  public String getNullCookie() {
    return String.valueOf(
        ResponseCookie.from(REFRESH_TOKEN.getKey(), null)
            .path("/")
            .maxAge(0)
            .httpOnly(true)
            .secure(true)
            .build());
  }

  public String getRefreshToken(HttpServletRequest request) {
    String refreshToken = null;

    Cookie[] cookies = request.getCookies();
    log.info("===== 로그아웃 요청 {} =====", Arrays.toString(cookies));
    for (Cookie cookie : cookies) {
      log.info("===== Cookie : {} =====", cookie);
      if (cookie.getName().equals(REFRESH_TOKEN.getKey())) {
        refreshToken = cookie.getValue();
      }
    }
    return refreshToken;
  }
}
