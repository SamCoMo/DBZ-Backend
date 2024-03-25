package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.ALREADY_LOGGED_OUT;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

  private final JwtUtil jwtUtil;

  private static final String LOGOUT_URI = "\\/member\\/logout";
  private static final String LOGOUT_METHOD = "POST";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {

    if (!isLogoutRequest(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String refreshToken = getRefreshToken(request);
    validateRefreshToken(refreshToken);

    jwtUtil.deleteRefreshTokenFromDataBase(refreshToken);
    deleteCookie(response);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private void validateRefreshToken(String refreshToken) {
    jwtUtil.validateTokenAndTokenType(refreshToken, REFRESH_TOKEN);
    if (!isTokenInDataBase(refreshToken)) {
      throw new MemberException(ALREADY_LOGGED_OUT);
    }
  }
  private void deleteCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(REFRESH_TOKEN.getKey(), null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    // cookie.setSecure(true);
    response.addCookie(cookie);
  }

  private String getRefreshToken(HttpServletRequest request) {
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

  private boolean isLogoutRequest(HttpServletRequest request) {
    if (!request.getMethod().equals(LOGOUT_METHOD)) {
      return false;
    }
    return request.getRequestURI().matches(LOGOUT_URI);
  }

  private boolean isTokenInDataBase(String refreshToken) {
    return jwtUtil.isRefreshTokenInDataBase(refreshToken);
  }
}
